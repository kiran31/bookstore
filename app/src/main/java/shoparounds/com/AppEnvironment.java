package shoparounds.com;



public enum AppEnvironment {

    SANDBOX {
        @Override
        public String merchant_Key() {
            return "lf6SBJcW";
        }

        @Override
        public String merchant_ID() {
            return "6760288";
        }

        @Override
        public String furl() {
            return "https://www.payumoney.com/mobileapp/payumoney/failure.php";
        }

        @Override
        public String surl() {
            return "https://www.payumoney.com/mobileapp/payumoney/success.php";
        }

        @Override
        public String salt() {
            return "5Aoh9nwnlt";
        }

        @Override
        public boolean debug() {
            return true;
        }
    },
    PRODUCTION {
        @Override
        public String merchant_Key() {
            return "lf6SBJcW";
        }
        @Override
        public String merchant_ID() {
            return "6760288";
        }
        @Override
        public String furl() {
            return "https://www.payumoney.com/mobileapp/payumoney/failure.php";
        }

        @Override
        public String surl() {
            return "https://www.payumoney.com/mobileapp/payumoney/success.php";
        }

        @Override
        public String salt() {
            return "5Aoh9nwnlt";
        }

        @Override
        public boolean debug() {
            return true;
        }
    };

    public abstract String merchant_Key();

    public abstract String merchant_ID();

    public abstract String furl();

    public abstract String surl();

    public abstract String salt();

    public abstract boolean debug();


}
