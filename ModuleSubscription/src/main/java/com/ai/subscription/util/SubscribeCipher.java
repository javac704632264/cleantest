package com.ai.subscription.util;

public class SubscribeCipher {

    // subscribe
    public static String fileName() {
        return (new Object() {
            int t;

            public String toString() {
                byte[] buf = new byte[9];
                t = 1134224281;
                buf[0] = (byte) (t >>> 3);
                t = -1211130556;
                buf[1] = (byte) (t >>> 6);
                t = 234113220;
                buf[2] = (byte) (t >>> 1);
                t = 566462269;
                buf[3] = (byte) (t >>> 4);
                t = -216038051;
                buf[4] = (byte) (t >>> 19);
                t = -1185157387;
                buf[5] = (byte) (t >>> 23);
                t = 372561829;
                buf[6] = (byte) (t >>> 15);
                t = 415075976;
                buf[7] = (byte) (t >>> 22);
                t = -245800496;
                buf[8] = (byte) (t >>> 14);
                return new String(buf);
            }
        }.toString());
    }

    //query_subscribe_count
    public static String keyQuerySubscribeCount() {
        return (new Object() {
            int t;

            public String toString() {
                byte[] buf = new byte[21];
                t = -1222717926;
                buf[0] = (byte) (t >>> 20);
                t = 1969677475;
                buf[1] = (byte) (t >>> 24);
                t = -1954861720;
                buf[2] = (byte) (t >>> 11);
                t = -1403213234;
                buf[3] = (byte) (t >>> 14);
                t = -908338957;
                buf[4] = (byte) (t >>> 1);
                t = -872953317;
                buf[5] = (byte) (t >>> 21);
                t = -113010561;
                buf[6] = (byte) (t >>> 11);
                t = 753384118;
                buf[7] = (byte) (t >>> 7);
                t = 1982749260;
                buf[8] = (byte) (t >>> 20);
                t = -76313262;
                buf[9] = (byte) (t >>> 16);
                t = 1669305050;
                buf[10] = (byte) (t >>> 24);
                t = 1115063188;
                buf[11] = (byte) (t >>> 3);
                t = -708151660;
                buf[12] = (byte) (t >>> 4);
                t = -2138765116;
                buf[13] = (byte) (t >>> 1);
                t = 1491707595;
                buf[14] = (byte) (t >>> 1);
                t = -167878376;
                buf[15] = (byte) (t >>> 20);
                t = -647814507;
                buf[16] = (byte) (t >>> 16);
                t = -320916830;
                buf[17] = (byte) (t >>> 17);
                t = -550913369;
                buf[18] = (byte) (t >>> 7);
                t = -1226075148;
                buf[19] = (byte) (t >>> 20);
                t = -2065129096;
                buf[20] = (byte) (t >>> 17);
                return new String(buf);
            }
        }.toString());
    }

    // sub_success_product_id
    public static String keySubSuccessProductId() {
        return (new Object() {
            int t;

            public String toString() {
                byte[] buf = new byte[22];
                t = -1354668825;
                buf[0] = (byte) (t >>> 1);
                t = -1729405688;
                buf[1] = (byte) (t >>> 17);
                t = -1392942499;
                buf[2] = (byte) (t >>> 8);
                t = -456789935;
                buf[3] = (byte) (t >>> 12);
                t = -587812084;
                buf[4] = (byte) (t >>> 22);
                t = -2145608342;
                buf[5] = (byte) (t >>> 6);
                t = -1767273280;
                buf[6] = (byte) (t >>> 10);
                t = -1607364708;
                buf[7] = (byte) (t >>> 15);
                t = -194810704;
                buf[8] = (byte) (t >>> 5);
                t = 1913216198;
                buf[9] = (byte) (t >>> 6);
                t = -644035989;
                buf[10] = (byte) (t >>> 14);
                t = 401730869;
                buf[11] = (byte) (t >>> 22);
                t = -1426651760;
                buf[12] = (byte) (t >>> 12);
                t = 544553336;
                buf[13] = (byte) (t >>> 7);
                t = -1681136829;
                buf[14] = (byte) (t >>> 22);
                t = -1773814286;
                buf[15] = (byte) (t >>> 20);
                t = -582091459;
                buf[16] = (byte) (t >>> 22);
                t = 962170097;
                buf[17] = (byte) (t >>> 10);
                t = -2041669021;
                buf[18] = (byte) (t >>> 13);
                t = 1607650815;
                buf[19] = (byte) (t >>> 24);
                t = -1553351109;
                buf[20] = (byte) (t >>> 16);
                t = -1801263416;
                buf[21] = (byte) (t >>> 1);
                return new String(buf);
            }
        }.toString());
    }

    // sub_success_price
    public static String keySubSuccessPrice() {
        return (new Object() {
            int t;

            public String toString() {
                byte[] buf = new byte[17];
                t = 426980770;
                buf[0] = (byte) (t >>> 16);
                t = -508732078;
                buf[1] = (byte) (t >>> 6);
                t = 1166762559;
                buf[2] = (byte) (t >>> 18);
                t = 405773176;
                buf[3] = (byte) (t >>> 15);
                t = 1916374076;
                buf[4] = (byte) (t >>> 15);
                t = -2096673324;
                buf[5] = (byte) (t >>> 2);
                t = -386753337;
                buf[6] = (byte) (t >>> 1);
                t = 147321630;
                buf[7] = (byte) (t >>> 3);
                t = -637183663;
                buf[8] = (byte) (t >>> 6);
                t = -634160228;
                buf[9] = (byte) (t >>> 3);
                t = 1558688873;
                buf[10] = (byte) (t >>> 17);
                t = 1204551258;
                buf[11] = (byte) (t >>> 13);
                t = 498606442;
                buf[12] = (byte) (t >>> 15);
                t = -1256601219;
                buf[13] = (byte) (t >>> 10);
                t = -1565218183;
                buf[14] = (byte) (t >>> 15);
                t = 163679837;
                buf[15] = (byte) (t >>> 10);
                t = -195579149;
                buf[16] = (byte) (t >>> 7);
                return new String(buf);
            }
        }.toString());
    }

    // subscribe_success_status
    public static String keySubscribeSuccessStatus() {
        return (new Object() {
            int t;

            public String toString() {
                byte[] buf = new byte[24];
                t = 967136632;
                buf[0] = (byte) (t >>> 23);
                t = 1747118827;
                buf[1] = (byte) (t >>> 1);
                t = 742866400;
                buf[2] = (byte) (t >>> 21);
                t = -1801657735;
                buf[3] = (byte) (t >>> 14);
                t = 1819439770;
                buf[4] = (byte) (t >>> 21);
                t = -1548143345;
                buf[5] = (byte) (t >>> 15);
                t = 129277039;
                buf[6] = (byte) (t >>> 15);
                t = -128175840;
                buf[7] = (byte) (t >>> 7);
                t = 1968594258;
                buf[8] = (byte) (t >>> 12);
                t = -492440754;
                buf[9] = (byte) (t >>> 12);
                t = -325049443;
                buf[10] = (byte) (t >>> 3);
                t = 1269362962;
                buf[11] = (byte) (t >>> 19);
                t = 1060232828;
                buf[12] = (byte) (t >>> 15);
                t = -825201566;
                buf[13] = (byte) (t >>> 5);
                t = -1711067969;
                buf[14] = (byte) (t >>> 5);
                t = 278604006;
                buf[15] = (byte) (t >>> 1);
                t = 806424180;
                buf[16] = (byte) (t >>> 5);
                t = -1344504664;
                buf[17] = (byte) (t >>> 23);
                t = -400080996;
                buf[18] = (byte) (t >>> 12);
                t = 487907578;
                buf[19] = (byte) (t >>> 22);
                t = 971739917;
                buf[20] = (byte) (t >>> 3);
                t = -1952078595;
                buf[21] = (byte) (t >>> 19);
                t = 1650130155;
                buf[22] = (byte) (t >>> 1);
                t = -1398574320;
                buf[23] = (byte) (t >>> 8);
                return new String(buf);
            }
        }.toString());
    }

    // subscribe_success_time
    public static String keySubscribeSuccessTime() {
        return (new Object() {
            int t;

            public String toString() {
                byte[] buf = new byte[22];
                t = 1199856893;
                buf[0] = (byte) (t >>> 6);
                t = 1001318669;
                buf[1] = (byte) (t >>> 19);
                t = -1516926156;
                buf[2] = (byte) (t >>> 10);
                t = 129427010;
                buf[3] = (byte) (t >>> 9);
                t = -1417171770;
                buf[4] = (byte) (t >>> 1);
                t = -1946229872;
                buf[5] = (byte) (t >>> 3);
                t = -1353894048;
                buf[6] = (byte) (t >>> 13);
                t = -616996076;
                buf[7] = (byte) (t >>> 3);
                t = -2061920842;
                buf[8] = (byte) (t >>> 10);
                t = -336104664;
                buf[9] = (byte) (t >>> 21);
                t = -1708225634;
                buf[10] = (byte) (t >>> 3);
                t = 2057823145;
                buf[11] = (byte) (t >>> 3);
                t = -1336570145;
                buf[12] = (byte) (t >>> 10);
                t = 743761094;
                buf[13] = (byte) (t >>> 1);
                t = 1663780047;
                buf[14] = (byte) (t >>> 19);
                t = 484962599;
                buf[15] = (byte) (t >>> 22);
                t = -1415904772;
                buf[16] = (byte) (t >>> 19);
                t = -1973558531;
                buf[17] = (byte) (t >>> 3);
                t = -688008519;
                buf[18] = (byte) (t >>> 10);
                t = 771967833;
                buf[19] = (byte) (t >>> 11);
                t = 1126062518;
                buf[20] = (byte) (t >>> 2);
                t = 847446110;
                buf[21] = (byte) (t >>> 23);
                return new String(buf);
            }
        }.toString());
    }
}
