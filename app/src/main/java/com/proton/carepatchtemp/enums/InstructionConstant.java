package com.proton.carepatchtemp.enums;

/**
 * Created by yuxiongfeng.
 * Date: 2019/8/20
 */
public enum InstructionConstant {
    /**
     * （1）输入指令“12ab”，显示算法版本号、算法阶段、姿势状态值，实时温度、温度曲线为算法温度+真实温度
     * <p>
     * （2）输入指令“11aa”，不显示算法阶段、姿势状态值，实时温度、温度曲线均为算法温度
     * <p>
     * （3）输入指令“22bb”，不显示算法阶段、姿势状态值，实时温度、温度曲线均为真实温度
     */
    ab("12ab"), aa("11aa"), bb("22bb");

    private String instruction;

    InstructionConstant(String s) {
        instruction = s;
    }

    public static InstructionConstant getInstructionConstant(String s) {
        for (InstructionConstant instructionConstant :
                values()) {
            if (s.equalsIgnoreCase(instructionConstant.getInstruction())) {
                return instructionConstant;
            }
        }
        return null;
    }

    public String getInstruction() {
        return instruction;
    }

}

