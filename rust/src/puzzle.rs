use rand::Rng;

pub struct Puzzle {
    pub a: i32,
    pub b: i32,
    pub answer: i32,
}
//math task !!!CHANGE HERE!!!
pub fn compute_answer(a: i32, b: i32) -> i32 {
    a * b
}

fn random_in_range(min: i32, max: i32) -> i32 {
    let (lo, hi) = if min <= max { (min, max) } else { (max, min) };
    rand::thread_rng().gen_range(lo..=hi)
}

pub fn generate(min: i32, max: i32) -> Puzzle {
    let a = random_in_range(min, max);
    let b = random_in_range(min, max);
    let answer = compute_answer(a, b);
    Puzzle { a, b, answer }
}

pub fn check(user_answer: i32, correct_answer: i32) -> bool {
    user_answer == correct_answer
}

#[cfg(test)]
mod tests {:wq
    use super::*;

    #[test]
    fn multiplication() {
        assert_eq!(compute_answer(7, 8), 56);
    }

    #[test]
    fn range_is_respected() {
        for _ in 0..1000 {
            let p = generate(2, 9);
            assert!((2..=9).contains(&p.a));
            assert!((2..=9).contains(&p.b));
            assert_eq!(p.answer, compute_answer(p.a, p.b));
        }
    }
}
