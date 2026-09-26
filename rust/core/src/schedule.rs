//! Расчёт времени следующего срабатывания будильника. Чистая арифметика.
//! Kotlin передаёт сюда System.currentTimeMillis() и смещение часового пояса,
//! получает готовый epoch-millis для AlarmManager.setExactAndAllowWhileIdle.

const DAY_MS: i64 = 24 * 60 * 60 * 1000;

/// `now_millis`       — System.currentTimeMillis() (UTC epoch, мс)
/// `tz_offset_millis` — TimeZone.getDefault().getOffset(now_millis) (мс)
/// `hour`/`minute`    — желаемое локальное время срабатывания
///
/// Возвращает epoch-millis (UTC) ближайшего будущего момента с этим локальным часом:минутой.
pub fn next_fire_time_millis(now_millis: i64, tz_offset_millis: i64, hour: i32, minute: i32) -> i64 {
    let local_now = now_millis + tz_offset_millis;
    let local_day_start = local_now - local_now.rem_euclid(DAY_MS);
    let target_local = local_day_start + (hour as i64) * 3_600_000 + (minute as i64) * 60_000;

    let target_local = if target_local <= local_now {
        target_local + DAY_MS
    } else {
        target_local
    };

    target_local - tz_offset_millis
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn schedules_later_today() {
        let now = 10 * 3_600_000i64; // 10:00 UTC
        let fire = next_fire_time_millis(now, 0, 15, 30);
        assert_eq!(fire, 15 * 3_600_000 + 30 * 60_000);
    }

    #[test]
    fn rolls_over_to_tomorrow_when_time_passed() {
        let now = 20 * 3_600_000i64; // 20:00 UTC, будильник на 07:00 -> завтра
        let fire = next_fire_time_millis(now, 0, 7, 0);
        assert_eq!(fire, DAY_MS + 7 * 3_600_000);
    }

    #[test]
    fn respects_timezone_offset() {
        let tz = 3 * 3_600_000i64; // например, Москва
        let now = 21 * 3_600_000i64; // 21:00 UTC = 00:00 местного
        let fire = next_fire_time_millis(now, tz, 7, 0); // будильник на 07:00 местного
        assert_eq!(fire, 4 * 3_600_000); // = 04:00 UTC
    }
}
