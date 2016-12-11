killall sclang
killall scsynth
kill `pgrep -f hilbertweb.py`
sleep 1
gnome-terminal -e 'sclang mouser.sc' &
gnome-terminal -e 'python hilbertweb.py' &
gnome-terminal -e 'chromium-browser http://127.0.0.1:5000' &
