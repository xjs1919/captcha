
package com.github.xjs.captcha.util;

import java.util.Random;

public class RandomUtil {

    public static int random(Random random, int min, int max){
        return random.nextInt(max - min + 1) + min;
    }
}
