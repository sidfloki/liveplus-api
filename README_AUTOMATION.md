# LivePlus Xtream Automation System 🚀

This system automatically fetches, validates, and organizes Xtream IPTV codes, movies, and series into a single M3U playlist. It's designed to run independently using GitHub Actions.

## 🛠 Features
- **Auto-Fetcher**: Scans public IPTV sources and generators for active Xtream credentials.
- **Validator**: Checks each code to ensure it is active (Status 200) before adding it to the list.
- **Content Scraper**: Fetches VOD (Movies) and Series content from active servers.
- **Smart Categorization**: Organizes items into categories using `#EXTGRP` tags (Sports, Movies, Series).
- **Poster Support**: Includes movie posters and series covers in the playlist.
- **Fully Automated**: Runs every 4 hours via GitHub Actions.

## 📁 Project Structure
- `scripts/xtream_fetcher.py`: The main logic script.
- `scripts/requirements.txt`: Python dependencies.
- `.github/workflows/auto_update.yml`: GitHub Actions configuration.
- `playlist.m3u`: The generated output file (automatically updated).

## 🚀 How to Setup
1. **Upload to GitHub**: Push all these files to your GitHub repository.
2. **Enable Actions**: Go to the **Actions** tab in your repository and ensure workflows are enabled.
3. **Permissions**: Make sure the GitHub Actions bot has permission to write to your repository. 
   - Go to `Settings` > `Actions` > `General`.
   - Scroll to `Workflow permissions`.
   - Select `Read and write permissions`.
   - Click `Save`.

## 🔄 Automatic Updates
The system is configured to run every **4 hours**. You can also trigger it manually:
- Go to the **Actions** tab.
- Click on **Auto Update Xtream Playlist**.
- Click **Run workflow**.

## 📺 Usage in APK
You can use the direct link to the `playlist.m3u` file in your app:
`https://raw.githubusercontent.com/YOUR_USERNAME/YOUR_REPO_NAME/main/playlist.m3u`

---
*Created for LivePlus Final Fixed Connection.*
