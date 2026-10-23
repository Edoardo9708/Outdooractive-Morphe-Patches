package lab.outdooractive.patches

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter

/**
 * Solo diagnostica per Outdooractive 3.23.20.
 * Conserva i risultati originali; non cambia account o autorizzazioni.
 *
 * Getter verificati su classes4.dex:
 * isProUser():Z -> iget-boolean v0,p0; return v0
 * getLevel():I  -> iget v0,p0; return v0
 */
@Suppress("unused")
val membershipDiagnosticPatch = bytecodePatch(
    name = "Outdooractive - Diagnostica abbonamento",
    description = "Registra in Logcat il livello e i controlli Pro, senza modificare i risultati.",
    default = false
) {
    compatibleWith(
        Compatibility(
            name = "Outdooractive",
            packageName = "com.outdooractive.Outdooractive",
            targets = listOf(AppTarget(version = "3.23.20"))
        )
    )

    execute {
        val target = "Lcom/outdooractive/sdk/objects/ooi/Membership;"
        val targetClass = mutableClassDefBy(target)

        fun instrumentGetter(
            name: String,
            opcode: Opcode,
            type: String,
            helper: String
        ) {
            val method = Fingerprint(definingClass = target, name = name).method
            val body = method.implementation ?: error("$name: missing bytecode")
            val instructions = body.instructions.toList()

            // Interrompi la patch se il DEX non corrisponde a quello studiato.
            require(body.registerCount == 1 && instructions.size == 2) {
                "$name: unexpected register or instruction layout"
            }
            val fieldRead = instructions[0] as? TwoRegisterInstruction
            val returning = instructions[1] as? OneRegisterInstruction
            require(
                instructions[0].opcode == opcode &&
                fieldRead?.registerA == 0 && fieldRead.registerB == 0 &&
                instructions[1].opcode == Opcode.RETURN &&
                returning?.registerA == 0 &&
                method.returnType == type
            ) {
                "$name: unexpected getter bytecode"
            }
            require(targetClass.methods.none { it.name == helper }) {
                "$name: diagnostic helper already exists"
            }

            // Invoca un logger void con v0; la return v0 originale resta intatta.
            method.addInstructions(1, "invoke-static {v0}, $target->$helper($type)V")
        }

        fun addLogger(helper: String, type: String, label: String) {
            // p0 è il registro 4; v0-v3 sono locali.
            targetClass.methods.add(
                ImmutableMethod(
                    target,
                    helper,
                    listOf(ImmutableMethodParameter(type, null, null)),
                    "V",
                    AccessFlags.PRIVATE.value or AccessFlags.STATIC.value,
                    null,
                    null,
                    MutableMethodImplementation(5)
                ).toMutable().apply {
                    addInstructions(
                        0,
                        """
                        const-string v0, "OA-Membership"
                        new-instance v1, Ljava/lang/StringBuilder;
                        invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V
                        const-string v2, "$label"
                        invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
                        invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append($type)Ljava/lang/StringBuilder;
                        invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
                        move-result-object v2
                        new-instance v3, Ljava/lang/Throwable;
                        invoke-direct {v3}, Ljava/lang/Throwable;-><init>()V
                        invoke-static {v0, v2, v3}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
                        return-void
                        """.trimIndent()
                    )
                }
            )
        }

        instrumentGetter("isProUser", Opcode.IGET_BOOLEAN, "Z", "morpheDiagPro")
        instrumentGetter("getLevel", Opcode.IGET, "I", "morpheDiagLevel")
        addLogger("morpheDiagPro", "Z", "isProUser = ")
        addLogger("morpheDiagLevel", "I", "getLevel = ")
    }
}
