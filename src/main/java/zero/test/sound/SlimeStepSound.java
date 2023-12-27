package zero.test.sound;
import net.minecraft.src.*;
public class SlimeStepSound extends StepSound {
    SlimeStepSound() {
        super("slime", 1.0f, 1.0f);
    }
    @Override
    public String getBreakSound() {
        return "mob.slime.big";
    }
    @Override
    public String getStepSound() {
        return "mob.slime.small";
    }
}
