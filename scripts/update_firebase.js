const data = {
  "xtream_config": {
    "host": "http://cineplay.vip:2086",
    "username": "SmvVyh9Hw4",
    "password": "yqQ9HHEpAm",
    "updated_at": "2026-05-08T15:45:00Z"
  },
  "source_list": {
    "server1": {
      "host": "http://cineplay.vip:2086",
      "username": "SmvVyh9Hw4",
      "password": "yqQ9HHEpAm"
    }
  },
  "commands": {
    "trigger_failover": false
  }
};

const url = "https://streamvault-5f4a7-default-rtdb.europe-west1.firebasedatabase.app/.json";

fetch(url, {
  method: 'PUT',
  body: JSON.stringify(data)
})
.then(res => res.json())
.then(json => {
  console.log("🔥 Firebase Updated Successfully:", json);
})
.catch(err => {
  console.error("❌ Firebase Update Failed:", err);
});
