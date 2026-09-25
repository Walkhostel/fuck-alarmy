use std::time::{SystemTime, UNIX_EPOCH};
use std::thread;
use std::time::Duration;

fn main() {
    let alarm_time = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_secs() + 30; //30s from now

    loop {
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_secs();

        if now >= alarm_time {
            //play custom sound (need libs)
            play_sound("alarm.wav");

            //show lock screen notification(Android shit)
            show_lock_screen();
            break;
        }
        thread::sleep(Duration::from_secs(1));
    }
}

fn play_sound(filename: &str) {
    //impl for playing custom sound file
    println!("Playing sound: {}", filename);
}

fn show_lock_screen() {
    //impl for displaying on lock screen
    println!("Displaying alarm on lock screen");
}
