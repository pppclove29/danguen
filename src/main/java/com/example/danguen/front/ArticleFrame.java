package com.example.danguen.front;

// for test
public class ArticleFrame {
    public enum Title {
        CellPhone, Mouse, Human, Chair, Brain, Bed, Ice, Cup, Car, Brick, Bus, iPhone, Fire, Magic, Mask;

        public static Title getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    public enum Content {
        내용입니다, 이건내용입니다, 가격이쌉니다, 없어서못팝니다, 마감임박, 네고사절, 싸다싸;

        public static Content getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    private static int[] Price = {
            10000, 20000, 30000, 40000, 500006, 401024, 234252, 21623, 23536, 2341235
    };

    public static int getRandomPrice() {
        return Price[(int) (Math.random() * Price.length)];
    }

    public enum City {
        서울시, 부산시, 대구시, 뉴욕, 모스크바, 런던, 하남시, 강원도, 제주도, 평양, 상하이, 도쿄;

        public static City getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    public enum Street {
        꽃길로, 비만로, 커피로, 산책로, 길로, 로로로로, 스트리트, 길거리, 길, 길이요;

        public static Street getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    public enum Zipcode {
        우편번호1, 우편번호2, 우편번호3, 우편번호4, 우편번호5, 우편번호6, 우편번호7, 우편번호8, 우편번호9;

        public static Zipcode getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }
}
