//! Весь JNI — 3 функции, только примитивы (int/long/bool) на входе-выходе.
//! Никаких вызовов Android API отсюда нет — этим занимается Kotlin.
//! Соответствие именам: com.example.mathalarm.NativeCore (см. NativeCore.kt).

use jni::objects::JClass;
use jni::sys::{jboolean, jint, jintArray, jlong};
use jni::JNIEnv;
use mathalarm_core::{puzzle, schedule};

/// NativeCore.generate(min, max) -> IntArray[3] = [a, b, answer]
#[no_mangle]
pub extern "system" fn Java_com_example_mathalarm_NativeCore_generate(
    env: JNIEnv,
    _class: JClass,
    min: jint,
    max: jint,
) -> jintArray {
    let p = puzzle::generate(min, max);
    let arr = env.new_int_array(3).expect("new_int_array");
    env.set_int_array_region(arr, 0, &[p.a, p.b, p.answer])
        .expect("set_int_array_region");
    arr
}

/// NativeCore.checkAnswer(userAnswer, correctAnswer) -> Boolean
#[no_mangle]
pub extern "system" fn Java_com_example_mathalarm_NativeCore_checkAnswer(
    _env: JNIEnv,
    _class: JClass,
    user_answer: jint,
    correct_answer: jint,
) -> jboolean {
    puzzle::check(user_answer, correct_answer) as jboolean
}

/// NativeCore.nextFireTimeMillis(nowMillis, tzOffsetMillis, hour, minute) -> Long
#[no_mangle]
pub extern "system" fn Java_com_example_mathalarm_NativeCore_nextFireTimeMillis(
    _env: JNIEnv,
    _class: JClass,
    now_millis: jlong,
    tz_offset_millis: jlong,
    hour: jint,
    minute: jint,
) -> jlong {
    schedule::next_fire_time_millis(now_millis, tz_offset_millis, hour, minute)
}
