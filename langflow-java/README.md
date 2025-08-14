# LangFlow Java (Spring Boot + Spring AI)

Maven + Spring Boot 3.3 + Spring AI (OpenAI) 版本的 LangFlow 最小实现：
- Flow/Node/Edge 模型
- 内存存储与 CRUD API
- DAG 执行器
- LLM 节点（通过 Spring AI ChatClient）

## 运行

1. 设置 OpenAI Key（可选，仅在使用 LLM 节点时需要）：

```bash
export OPENAI_API_KEY=sk-xxxxx
export SPRING_AI_OPENAI_ENABLED=true
```

2. 本地构建与运行（建议使用 Maven Wrapper 或系统 Maven）：

```bash
./mvnw spring-boot:run
```

若没有包装器，可安装 Maven 或下载 Maven 二进制后运行：

```bash
mvn spring-boot:run
```

应用启动后访问：
- 健康检查: http://localhost:8080/actuator/health
- Flow API 根: http://localhost:8080/api/flows

## 示例

- 创建 Flow：POST `/api/flows`，请求体参见 `Flow` 模型（包含一个 `llm` 节点）。
- 运行 Flow：POST `/api/flows/{id}/run`，请求体可包含 `inputs` 用于入口数据。

## 配置

见 `src/main/resources/application.yml`。你也可以通过环境变量覆盖：
- `OPENAI_API_KEY`
- `SPRING_AI_OPENAI_ENABLED`
- `SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL` 等。