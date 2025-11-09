4. Login with:
- **Lecturer**: lecturer / lecturer123
- **Student**: student / student123
- **Admin**: admin / admin123

### How to Use

- As Lecturer:
 - Start Video to see webcam feed
 - Start Recording (records video + audio)
 - Stop Recording (audio/video merged to MP4 using FFmpeg)
 - View saved/merged lectures in the recordings folder

- As Student:
 - Watch/play available lectures, read full transcript if available

- As Admin:
 - View system stats, manage users, clear cache, backup data

## Troubleshooting

- **Mic not detected:** Use Windows Sound settings to test mic. If Java can't see it but Windows can, check drivers, and try a USB headset.
- **No translation / API failure:** Demo uses mock translations for offline. Replace TranslationEngine for real API integration.
- **FFmpeg not working:** Download and add ffmpeg.exe to PATH, then restart PC. Verify by running `ffmpeg -version` in Command Prompt.

## Credits

- OpenCV, Sphinx4, LibreTranslate/Google Translate API (or mock), FFmpeg

## License

MIT
