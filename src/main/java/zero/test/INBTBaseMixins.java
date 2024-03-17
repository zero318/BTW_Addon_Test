package zero.test;
public interface INBTBaseMixins {
    default public void toSNBT(StringBuilder str) {
        str.append(this.toString());
    }
}
