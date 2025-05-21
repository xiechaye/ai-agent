package cn.edu.xmu.aiagent.agent.model;

public enum AgentState {
    /**
     * 空闲
     */
    IDLE,
    /**
     * 运行中
     */
    RUNNING,
    /**
     * 已完成任务
     */
    FINISHED,
    /**
     * 任务失败
     */
    ERROR
}
