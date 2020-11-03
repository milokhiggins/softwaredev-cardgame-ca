package cardgame;

public class MockAtomicInt {
    public int value;
    public MockAtomicInt(int value) {
        this.value = value;
    }
    public boolean compareAndSet(int expected, int value) {
        if (this.value == expected) {
            this.value = value;
            return true;
        } else {
            return false;
        }
    }
}
