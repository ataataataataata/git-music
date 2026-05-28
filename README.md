# git-music 🎵

> Automatically adds your currently playing Spotify song to every git commit message.

```
git commit -m "fix login bug"
# becomes:
git commit -m "fix login bug ♪ Motive - Alev"
```

---

## Requirements

- Java 17+
- Git
- Spotify Premium account

---

## Installation

### 1. Create a Spotify App

1. Go to [developer.spotify.com](https://developer.spotify.com)
2. Log in with your Spotify account
3. Click **Create App**
4. Fill in:
   - App name: anything you want
   - Redirect URI: `http://127.0.0.1:8888/callback`
   - API: check **Web API**
5. Save and copy your **Client ID** and **Client Secret**

### 2. Download the JAR

Download the latest `git-music.jar` from the [Releases](../../releases) page.

### 3. Run Setup

Navigate to your git project folder and run setup with the full path to the JAR:

```bash
# Windows
cd your-project
java -jar C:\path\to\git-music.jar setup

# Example
cd C:\my-project
java -jar C:\Users\YourName\Downloads\git-music.jar setup
```

```bash
# Mac / Linux
cd your-project
java -jar ~/Downloads/git-music.jar setup
```

You will be asked for:
- Your Spotify **Client ID**
- Your Spotify **Client Secret**

Then a browser link will open — log in to Spotify and accept. Paste the code from the URL into the terminal.

That's it! ✅

---

## Usage

Once setup is complete, every commit will automatically include the currently playing song:

```bash
git commit -m "refactor auth"
# result: refactor auth ♪ The Weeknd - Blinding Lights
```

If no song is playing, the commit message stays unchanged.

---

## How It Works

- On setup, a `prepare-commit-msg` Git hook is installed in your project's `.git/hooks/` folder
- The JAR is also copied to `~/git-music.jar` so it works from any project
- Every time you commit, the hook runs the JAR
- The JAR calls the Spotify API to get the currently playing track
- The track name is appended to your commit message

---

## Notes

- Setup needs to be run once per project
- Your Spotify token is stored locally and refreshes automatically
- Your credentials are stored in `~/git-music-config.txt`

---

## License

MIT
