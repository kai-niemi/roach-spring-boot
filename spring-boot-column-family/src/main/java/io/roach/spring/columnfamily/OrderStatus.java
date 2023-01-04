package io.roach.spring.columnfamily;

public enum OrderStatus {
    PLACED {
        @Override
        public OrderStatus next() {
            return CONFIRMED;
        }
    },
    CONFIRMED {
        @Override
        public OrderStatus next() {
            return PAID;
        }
    },
    PAID {
        @Override
        public OrderStatus next() {
            return TRANSIT;
        }
    },
    TRANSIT {
        @Override
        public OrderStatus next() {
            return CANCELLED;
        }
    },
    CANCELLED {
        @Override
        public OrderStatus next() {
            return OrderStatus.PLACED;
        }
    };

    public abstract OrderStatus next();
}
