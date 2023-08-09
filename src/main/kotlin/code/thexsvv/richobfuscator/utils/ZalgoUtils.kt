package code.thexsvv.richobfuscator.utils

import org.apache.commons.lang3.RandomStringUtils

class ZalgoUtils {

    companion object {
        private val ZALGO_CHARS = charArrayOf(
                '\u036D', '\u0307', '\u0343', '\u0313',
                '\u0309', '\u0357', '\u0322', '\u032A',
                '\u032D', '\u032C', '\u032E', '\u0321',
                '\u0322', '\u031D', '\u031D', '\u031C',
                '\u0323', '\u0328', '\u0329', '\u032C'
        );

        fun generateZalgo(length: Int): String {
            val stringBuilder = StringBuilder();

            for (i in 0 until length) {
                stringBuilder.append(RandomStringUtils.random(1));
                for (j in 0 until 150)
                    stringBuilder.append("\n").append(ZALGO_CHARS[RandomUtils.nextInt(0, ZALGO_CHARS.size-1)]);
            }

            return stringBuilder.toString();
        }
    }
}
