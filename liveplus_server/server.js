const express = require('express');
const path = require('express');
const fs = require('fs');
const app = express();
const PORT = 3000;

app.use(express.json());
app.use(express.static('public'));

// Mock Database for media
let mediaData = {
    movies: [
        { id: 1, title: "فيلم الأكشن الأول", category: "ACTION", url: "/stream/movie1.mp4", poster: "https://via.placeholder.com/200x300" },
        { id: 2, title: "عالم الفانتازيا", category: "FNTACY", url: "/stream/movie2.mp4", poster: "https://via.placeholder.com/200x300" }
    ],
    series: [],
    channels: [
        { id: 101, title: "قناة الجزيرة", url: "https://live-hls-web-aje.akamaized.net/m3u8/aje/ar/main/aje_ar_main_6.m3u8" }
    ]
};

// API: Get all media
app.get('/api/media', (req, res) => {
    res.json(mediaData);
});

// Admin Dashboard Route
app.get('/admin', (req, res) => {
    res.sendFile(__dirname + '/public/dashboard.html');
});

// Start Server
app.listen(PORT, '0.0.0.0', () => {
    console.log(`LIVEPLUS Server running at http://localhost:${PORT}`);
    console.log(`Mobile App should connect to http://YOUR_PC_IP:${PORT}`);
});
