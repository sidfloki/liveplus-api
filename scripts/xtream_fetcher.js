/**
 * LivePlus - Xtream Codes Auto Fetcher (Node.js Version)
 * For users who don't have Python installed.
 */

const FIREBASE_DB_URL = "https://streamvault-5f4a7-default-rtdb.europe-west1.firebasedatabase.app/xtream_config.json";

const KNOWN_SERVERS = [
    {host: "http://cineplay.vip:2086", username: "SmvVyh9Hw4", password: "yqQ9HHEpAm"},
    {host: "http://hydratv.pro:2095", username: "hp9559326", password: "966568763228"},
    {host: "http://hydratv.pro:80", username: "yaserabuamar2024", password: "18032024yaser"},
    {host: "http://22ahmed6.geekflarecdn.com:80", username: "22ahmed6", password: "8632682"},
    {host: "http://22almallah.geekflarecdn.com", username: "22almallah", password: "h9369463"},
    {host: "http://2adam2012.geekflarecdn.com:80", username: "2adam2012", password: "amiradam2012"},
    {host: "http://48m07md.geekflarecdn.com:80", username: "48m07md", password: "m9430158"},
    {host: "http://5owlood99.geekflarecdn.com:80", username: "5owlood99", password: "3673053"},
    {host: "http://abdal4ah.geekflarecdn.com:80", username: "abdal4ah", password: "6036yv9dh"},
    {host: "http://abdelfatah76g.geekflarecdn.com:80", username: "abdelfatah76g", password: "51203816"},
    {host: "http://abdullla5672.geekflarecdn.com:80", username: "abdullla5672", password: "6654241"},
    {host: "http://ade4398ahmed.geekflarecdn.com:80", username: "ade4398ahmed", password: "39457843"},
    {host: "http://ahmad234g.geekflarecdn.com:80", username: "ahmad234g", password: "631567295"},
    {host: "http://Sameh68g.geekflarecdn.com", username: "sameh68g", password: "15472848"}
];

async function validateServer(server) {
    const host = server.host.replace(/\/$/, "");
    const authUrl = `${host}/player_api.php?username=${server.username}&password=${server.password}`;
    
    try {
        const response = await fetch(authUrl, { 
            headers: { "User-Agent": "IPTVSmartersPlayer" },
            signal: AbortSignal.timeout(10000)
        });
        
        if (response.ok) {
            const data = await response.json();
            const userInfo = data.user_info || {};
            if (userInfo.status === "Active" || userInfo.auth === 1) {
                console.log(`✅ ACTIVE: ${host} (Expires: ${userInfo.exp_date || "N/A"})`);
                return { ...server, user_info: userInfo };
            }
        }
    } catch (e) {
        console.log(`❌ Failed: ${host}`);
    }
    return null;
}

async function main() {
    console.log("🚀 Starting Node.js Xtream Fetcher...");
    
    let validServers = [];
    for (const server of KNOWN_SERVERS) {
        const result = await validateServer(server);
        if (result) validServers.push(result);
    }
    
    if (validServers.length > 0) {
        // Sort by expiry date
        validServers.sort((a, b) => {
            const dateA = a.user_info.exp_date || "0";
            const dateB = b.user_info.exp_date || "0";
            return dateB.localeCompare(dateA);
        });
        
        const best = validServers[0];
        const creds = {
            host: best.host,
            username: best.username,
            password: best.password,
            updated_at: new Date().toISOString()
        };
        
        console.log("\n🔑 Best Server Found:", best.host);
        
        // Update Firebase
        try {
            const fbResp = await fetch(FIREBASE_DB_URL, {
                method: 'PUT',
                body: JSON.stringify(creds)
            });
            if (fbResp.ok) {
                console.log("🔥 Successfully synced to Firebase!");
            } else {
                console.log("⚠️ Firebase sync failed:", fbResp.status);
            }
        } catch (e) {
            console.log("❌ Firebase Error:", e.message);
        }
    } else {
        console.log("❌ No active servers found.");
    }
}

main();
