#!/usr/bin/env python3
"""
LivePlus - Xtream Codes Auto Fetcher & Playlist Generator
Fetches Xtream codes from public generators, validates them,
and builds a categorized M3U playlist with VOD content.
"""

import requests
import json
import time
import re
import os
import logging
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime

# ─── Configuration ───────────────────────────────────────────
TIMEOUT = 10
MAX_WORKERS = 15
MAX_VOD_PER_SERVER = 500
MAX_SERIES_PER_SERVER = 300
OUTPUT_FILE = "playlist.m3u"
LOG_FILE = "scripts/fetch_log.txt"

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger("XtreamFetcher")

# ─── Known Public Xtream Generator Sources ───────────────────
# These are common patterns for free/public xtream code sites
GENERATOR_URLS = [
    "https://iptvxtreamcodes.com/api/generate",
    "https://freeiptv-m3u.com/xtream-codes/",
    "https://iptv-org.github.io/iptv/index.m3u",
    "https://raw.githubusercontent.com/iptv-org/iptv/master/streams/us.m3u",
    "https://raw.githubusercontent.com/Free-TV/IPTV/master/playlist.m3u8",
]

# Hardcoded known working servers (from Config.kt backup)
KNOWN_SERVERS = [
    {
        "host": "http://Sameh68g.geekflarecdn.com",
        "username": "sameh68g",
        "password": "15472848"
    }
]


def extract_xtream_from_m3u(content):
    """Extract Xtream credentials from M3U file content."""
    codes = []
    # Pattern: http://host:port/username/password/stream_id
    pattern = r'https?://([^/\s]+(?::\d+)?)/(?:live/|movie/|series/)?([^/\s]+)/([^/\s]+)/\d+'
    matches = re.findall(pattern, content)
    seen = set()
    for host, user, pwd in matches:
        key = f"{host}|{user}|{pwd}"
        if key not in seen and len(user) > 1 and len(pwd) > 1:
            seen.add(key)
            proto = "https" if "443" in host else "http"
            codes.append({
                "host": f"{proto}://{host}",
                "username": user,
                "password": pwd
            })
    return codes


def extract_xtream_from_page(content):
    """Extract Xtream codes from HTML pages with common patterns."""
    codes = []
    # Pattern for typical generator output
    host_pattern = r'(?:host|server|url|dns)["\s:=]+\s*(https?://[^\s"<,]+)'
    user_pattern = r'(?:user(?:name)?|login)["\s:=]+\s*([^\s"<,]+)'
    pass_pattern = r'(?:pass(?:word)?)["\s:=]+\s*([^\s"<,]+)'

    hosts = re.findall(host_pattern, content, re.IGNORECASE)
    users = re.findall(user_pattern, content, re.IGNORECASE)
    passwords = re.findall(pass_pattern, content, re.IGNORECASE)

    for i in range(min(len(hosts), len(users), len(passwords))):
        codes.append({
            "host": hosts[i].rstrip("/"),
            "username": users[i],
            "password": passwords[i]
        })
    return codes


def fetch_codes_from_generators():
    """Fetch Xtream codes from all generator sources."""
    all_codes = list(KNOWN_SERVERS)
    seen = set()
    for srv in KNOWN_SERVERS:
        seen.add(f"{srv['host']}|{srv['username']}|{srv['password']}")

    for url in GENERATOR_URLS:
        try:
            logger.info(f"Fetching from: {url}")
            resp = requests.get(url, timeout=TIMEOUT, headers={
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
            })
            if resp.status_code == 200:
                content = resp.text
                # Try M3U extraction
                if "#EXTM3U" in content or "#EXTINF" in content:
                    found = extract_xtream_from_m3u(content)
                else:
                    found = extract_xtream_from_page(content)
                    # Also try M3U patterns in HTML
                    found += extract_xtream_from_m3u(content)

                for code in found:
                    key = f"{code['host']}|{code['username']}|{code['password']}"
                    if key not in seen:
                        seen.add(key)
                        all_codes.append(code)

                logger.info(f"  Found {len(found)} codes from {url}")
        except Exception as e:
            logger.warning(f"  Failed to fetch {url}: {e}")

    logger.info(f"Total unique codes collected: {len(all_codes)}")
    return all_codes


def validate_server(server):
    """Validate an Xtream server by checking authentication."""
    host = server["host"].rstrip("/")
    user = server["username"]
    pwd = server["password"]
    auth_url = f"{host}/player_api.php?username={user}&password={pwd}"

    try:
        resp = requests.get(auth_url, timeout=TIMEOUT, headers={
            "User-Agent": "Mozilla/5.0"
        })
        if resp.status_code == 200:
            data = resp.json()
            user_info = data.get("user_info", {})
            status = user_info.get("status", "")
            auth_val = user_info.get("auth", 0)

            if status == "Active" or auth_val == 1:
                exp = user_info.get("exp_date", "N/A")
                logger.info(f"  ✅ ACTIVE: {host} (user: {user}, expires: {exp})")
                server["server_info"] = data.get("server_info", {})
                server["user_info"] = user_info
                return server
            else:
                logger.info(f"  ❌ Inactive: {host} (status: {status})")
        else:
            logger.info(f"  ❌ HTTP {resp.status_code}: {host}")
    except Exception as e:
        logger.info(f"  ❌ Error: {host} - {e}")

    return None


def fetch_vod_content(server):
    """Fetch VOD (movies) from a validated server."""
    host = server["host"].rstrip("/")
    user = server["username"]
    pwd = server["password"]
    movies = []

    try:
        # Fetch VOD categories
        cat_url = f"{host}/player_api.php?username={user}&password={pwd}&action=get_vod_categories"
        cat_resp = requests.get(cat_url, timeout=TIMEOUT)
        categories = {}
        if cat_resp.status_code == 200:
            try:
                cat_data = cat_resp.json()
                if isinstance(cat_data, list):
                    categories = {str(c.get("category_id", "")): c.get("category_name", "Unknown") for c in cat_data}
            except:
                pass

        # Fetch VOD streams
        vod_url = f"{host}/player_api.php?username={user}&password={pwd}&action=get_vod_streams"
        vod_resp = requests.get(vod_url, timeout=TIMEOUT + 10)
        if vod_resp.status_code == 200:
            try:
                vod_data = vod_resp.json()
                if isinstance(vod_data, list):
                    for item in vod_data[:MAX_VOD_PER_SERVER]:
                        stream_id = item.get("stream_id", "")
                        name = item.get("name", "Unknown Movie")
                        ext = item.get("container_extension", "mp4")
                        cat_id = str(item.get("category_id", ""))
                        cat_name = categories.get(cat_id, "أفلام")
                        poster = item.get("stream_icon", "")
                        rating = item.get("rating", "")

                        stream_url = f"{host}/movie/{user}/{pwd}/{stream_id}.{ext}"
                        movies.append({
                            "name": name,
                            "url": stream_url,
                            "category": cat_name,
                            "poster": poster,
                            "rating": rating,
                            "type": "movie",
                            "server": host
                        })
            except:
                pass

        logger.info(f"  🎬 Fetched {len(movies)} movies from {host}")
    except Exception as e:
        logger.warning(f"  Failed to fetch VOD from {host}: {e}")

    return movies


def fetch_series_content(server):
    """Fetch series from a validated server."""
    host = server["host"].rstrip("/")
    user = server["username"]
    pwd = server["password"]
    series_list = []

    try:
        # Fetch series categories
        cat_url = f"{host}/player_api.php?username={user}&password={pwd}&action=get_series_categories"
        cat_resp = requests.get(cat_url, timeout=TIMEOUT)
        categories = {}
        if cat_resp.status_code == 200:
            try:
                cat_data = cat_resp.json()
                if isinstance(cat_data, list):
                    categories = {str(c.get("category_id", "")): c.get("category_name", "Unknown") for c in cat_data}
            except:
                pass

        # Fetch series list
        series_url = f"{host}/player_api.php?username={user}&password={pwd}&action=get_series"
        series_resp = requests.get(series_url, timeout=TIMEOUT + 10)
        if series_resp.status_code == 200:
            try:
                series_data = series_resp.json()
                if isinstance(series_data, list):
                    for item in series_data[:MAX_SERIES_PER_SERVER]:
                        series_id = item.get("series_id", "")
                        name = item.get("name", "Unknown Series")
                        cat_id = str(item.get("category_id", ""))
                        cat_name = categories.get(cat_id, "مسلسلات")
                        poster = item.get("cover", "") or item.get("stream_icon", "")
                        rating = item.get("rating", "")

                        # Fetch episodes for this series
                        try:
                            info_url = f"{host}/player_api.php?username={user}&password={pwd}&action=get_series_info&series_id={series_id}"
                            info_resp = requests.get(info_url, timeout=TIMEOUT)
                            if info_resp.status_code == 200:
                                info_data = info_resp.json()
                                episodes = info_data.get("episodes", {})
                                for season_num, eps in episodes.items():
                                    if isinstance(eps, list):
                                        for ep in eps[:20]:  # Limit episodes per season
                                            ep_id = ep.get("id", "")
                                            ep_title = ep.get("title", f"Episode {ep.get('episode_num', '?')}")
                                            ep_ext = ep.get("container_extension", "mp4")
                                            ep_url = f"{host}/series/{user}/{pwd}/{ep_id}.{ep_ext}"

                                            series_list.append({
                                                "name": f"{name} - S{season_num}E{ep.get('episode_num', '?')} - {ep_title}",
                                                "url": ep_url,
                                                "category": cat_name,
                                                "poster": poster,
                                                "rating": rating,
                                                "type": "series",
                                                "server": host
                                            })
                                time.sleep(0.3)  # Rate limiting
                        except:
                            pass
            except:
                pass

        logger.info(f"  📺 Fetched {len(series_list)} series episodes from {host}")
    except Exception as e:
        logger.warning(f"  Failed to fetch series from {host}: {e}")

    return series_list


def fetch_live_channels(server):
    """Fetch live TV channels from a validated server."""
    host = server["host"].rstrip("/")
    user = server["username"]
    pwd = server["password"]
    channels = []

    try:
        cat_url = f"{host}/player_api.php?username={user}&password={pwd}&action=get_live_categories"
        cat_resp = requests.get(cat_url, timeout=TIMEOUT)
        categories = {}
        if cat_resp.status_code == 200:
            try:
                cat_data = cat_resp.json()
                if isinstance(cat_data, list):
                    categories = {str(c.get("category_id", "")): c.get("category_name", "Unknown") for c in cat_data}
            except:
                pass

        live_url = f"{host}/player_api.php?username={user}&password={pwd}&action=get_live_streams"
        live_resp = requests.get(live_url, timeout=TIMEOUT + 10)
        if live_resp.status_code == 200:
            try:
                live_data = live_resp.json()
                if isinstance(live_data, list):
                    for item in live_data:
                        stream_id = item.get("stream_id", "")
                        name = item.get("name", "Unknown Channel")
                        cat_id = str(item.get("category_id", ""))
                        cat_name = categories.get(cat_id, "قنوات")
                        
                        # Basic sports detection
                        name_lower = name.lower()
                        if any(x in name_lower for x in ["sport", "bein", "ssc", "kora", "رياضة"]):
                            cat_name = "رياضة"
                            
                        poster = item.get("stream_icon", "")

                        stream_url = f"{host}/live/{user}/{pwd}/{stream_id}.m3u8"
                        channels.append({
                            "name": name,
                            "url": stream_url,
                            "category": cat_name,
                            "poster": poster,
                            "rating": "",
                            "type": "live",
                            "server": host
                        })
            except:
                pass

        logger.info(f"  📡 Fetched {len(channels)} live channels from {host}")
    except Exception as e:
        logger.warning(f"  Failed to fetch live channels from {host}: {e}")

    return channels


def build_m3u_playlist(all_content):
    """Build a categorized M3U playlist file."""
    now = datetime.utcnow().strftime("%Y-%m-%d %H:%M UTC")

    lines = [
        f'#EXTM3U url-tvg="https://iptv-org.github.io/epg/guides/ar.xml" refresh="3600"',
        f'# LivePlus Auto-Generated Playlist',
        f'# Updated: {now}',
        f'# Total items: {len(all_content)}',
        ''
    ]

    # Sort by type then category
    all_content.sort(key=lambda x: (
        {"live": 0, "movie": 1, "series": 2}.get(x["type"], 3),
        x["category"],
        x["name"]
    ))

    for item in all_content:
        name = item["name"]
        url = item["url"]
        cat = item["category"]
        poster = item.get("poster", "")
        rating = item.get("rating", "")
        item_type = item["type"]

        # Build EXTINF line with metadata
        tvg_logo = f' tvg-logo="{poster}"' if poster else ''
        group = f' group-title="{cat}"'

        # Type prefix for clarity
        type_prefix = ""
        if item_type == "movie":
            type_prefix = "🎬 "
        elif item_type == "series":
            type_prefix = "📺 "
        elif item_type == "live":
            type_prefix = "📡 "

        extinf = f'#EXTINF:-1{tvg_logo}{group},{type_prefix}{name}'
        lines.append(f'#EXTGRP:{cat}')
        lines.append(extinf)
        lines.append(url)
        lines.append('')

    return '\n'.join(lines)


def save_playlist(content, filepath):
    """Save playlist to file."""
    os.makedirs(os.path.dirname(filepath) if os.path.dirname(filepath) else '.', exist_ok=True)
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    logger.info(f"💾 Playlist saved to {filepath} ({len(content)} bytes)")


def save_log(valid_servers, total_content):
    """Save fetch log for debugging."""
    try:
        os.makedirs(os.path.dirname(LOG_FILE) if os.path.dirname(LOG_FILE) else '.', exist_ok=True)
        with open(LOG_FILE, 'w', encoding='utf-8') as f:
            f.write(f"LivePlus Fetch Log - {datetime.utcnow().strftime('%Y-%m-%d %H:%M UTC')}\n")
            f.write(f"{'='*60}\n")
            f.write(f"Active servers: {len(valid_servers)}\n")
            f.write(f"Total content items: {total_content}\n\n")
            for srv in valid_servers:
                exp = srv.get("user_info", {}).get("exp_date", "N/A")
                f.write(f"  Server: {srv['host']}\n")
                f.write(f"  User: {srv['username']}\n")
                f.write(f"  Expires: {exp}\n\n")
    except Exception as e:
        logger.warning(f"Failed to save log: {e}")


def main():
    """Main execution flow."""
    logger.info("=" * 60)
    logger.info("🚀 LivePlus Xtream Auto-Fetcher Starting...")
    logger.info("=" * 60)

    # Step 1: Fetch codes from generators
    logger.info("\n📡 Step 1: Fetching Xtream codes from generators...")
    all_codes = fetch_codes_from_generators()

    if not all_codes:
        logger.error("No codes found. Exiting.")
        return

    # Step 2: Validate servers in parallel
    logger.info(f"\n🔍 Step 2: Validating {len(all_codes)} servers...")
    valid_servers = []
    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        futures = {executor.submit(validate_server, srv): srv for srv in all_codes}
        for future in as_completed(futures):
            result = future.result()
            if result:
                valid_servers.append(result)

    logger.info(f"\n✅ {len(valid_servers)} active servers found out of {len(all_codes)}")

    if not valid_servers:
        logger.warning("No active servers. Creating empty playlist.")
        save_playlist('#EXTM3U\n# No active servers found\n', OUTPUT_FILE)
        return

    # Step 3: Fetch content from all valid servers
    logger.info("\n🎬 Step 3: Fetching VOD content from active servers...")
    all_content = []

    for server in valid_servers:
        # Fetch movies
        movies = fetch_vod_content(server)
        all_content.extend(movies)

        # Fetch series (limit to avoid timeout)
        series = fetch_series_content(server)
        all_content.extend(series)

        # Fetch live channels
        channels = fetch_live_channels(server)
        all_content.extend(channels)

    logger.info(f"\n📊 Total content fetched: {len(all_content)} items")
    movies_count = sum(1 for x in all_content if x["type"] == "movie")
    series_count = sum(1 for x in all_content if x["type"] == "series")
    live_count = sum(1 for x in all_content if x["type"] == "live")
    logger.info(f"  🎬 Movies: {movies_count}")
    logger.info(f"  📺 Series Episodes: {series_count}")
    logger.info(f"  📡 Live Channels: {live_count}")

    # Step 4: Build and save playlist
    logger.info("\n📝 Step 4: Building M3U playlist...")
    playlist = build_m3u_playlist(all_content)
    save_playlist(playlist, OUTPUT_FILE)

    # Save log
    save_log(valid_servers, len(all_content))

    logger.info("\n🎉 Done! Playlist generated successfully.")
    logger.info("=" * 60)


if __name__ == "__main__":
    main()
