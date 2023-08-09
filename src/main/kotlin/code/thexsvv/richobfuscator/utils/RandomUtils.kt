package code.thexsvv.richobfuscator.utils

import java.security.SecureRandom

class RandomUtils {

    companion object {
        private val random = SecureRandom();

        fun nextInt(): Int {
            return random.nextInt();
        }

        fun nextInt(bound: Int): Int {
            return random.nextInt(bound);
        }

        fun nextInt(origin: Int, bound: Int): Int {
            return random.nextInt(origin, bound);
        }

        fun nextLong(): Long {
            return random.nextLong();
        }

        fun nextLong(bound: Long): Long {
            return random.nextLong(bound);
        }

        fun nextLong(origin: Long, bound: Long): Long {
            return random.nextLong(origin, bound);
        }

        fun nextFloat(): Float {
            return random.nextFloat();
        }

        fun nextFloat(bound: Float): Float {
            return random.nextFloat(bound);
        }

        fun nextFloat(origin: Float, bound: Float): Float {
            return random.nextFloat(origin, bound);
        }

        fun nextDouble(): Double {
            return random.nextDouble();
        }

        fun nextDouble(bound: Double): Double {
            return random.nextDouble(bound);
        }

        fun nextDouble(origin: Double, bound: Double): Double {
            return random.nextDouble(origin, bound);
        }

        fun nextBoolean(): Boolean {
            return random.nextBoolean();
        }
    }
}
