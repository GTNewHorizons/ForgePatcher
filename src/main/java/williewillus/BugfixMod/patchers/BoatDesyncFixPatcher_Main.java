package williewillus.BugfixMod.patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.Iterator;

/**
 * Created by Vincent on 7/19/2014.
 */
public class BoatDesyncFixPatcher_Main extends AbstractPatcher implements ModificationPatcher {
    public BoatDesyncFixPatcher_Main(String name, String targetClassName, String targetMethodName, String targetMethodDesc) {
        super(name, targetClassName, targetMethodName, targetMethodDesc);
    }

    @Override
    public InsnList buildNewInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet) {
        return new InsnList();
    }

    @Override
    public void modifyInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet, InsnList instructions) {
        if (currentInstruction.getOpcode() == Opcodes.RETURN) {
            printMessage("Found entry point");
            instructions.remove(currentInstruction.getPrevious().getPrevious().getPrevious());
            instructions.remove(currentInstruction.getPrevious().getPrevious());
            instructions.remove(currentInstruction.getPrevious());
            successful = true;
            printMessage("Thanks to jonathan2520 on Mojira for the quick fix!");
        }
    }
}
