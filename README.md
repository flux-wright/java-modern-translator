# ModernTranslator — Java

A small, dependency-free translation client for Java 11+, built on the
built-in `java.net.http.HttpClient` (HTTP/2). Sync and async APIs against
any JSON translation endpoint (DeepL-style by default).

基于 JDK 内置 HttpClient（HTTP/2）的 Java 翻译客户端，零第三方依赖：同步 + 异步双接口，可对接 DeepL 或大模型翻译网关。

## Features

- **Zero dependencies** — 只用 JDK 自带的 `java.net.http`，HTTP/2、10 秒连接超时开箱即用
- **Sync + async** — `translateSync` 阻塞调用；`translateAsync` 返回 `CompletableFuture`，适合高并发/不阻塞主线程的场景
- **Endpoint-agnostic** — API 地址和 Key 都由构造参数传入，换供应商只换配置

## Usage

```java
ModernTranslator translator = new ModernTranslator(
    "your_api_key_here",                      // replace with a real key
    "https://api.deepL.com/v2/translate"
);

// Sync
String result = translator.translateSync("Hello, Java developer!", "ZH");

// Async
translator.translateAsync("Advanced software engineering.", "ZH")
          .thenAccept(res -> System.out.println(res))
          .join();
```

## Run the demo

Requires JDK 11 or later (JDK 17 LTS recommended).

```bash
javac -encoding UTF-8 ModernTranslator.java
java ModernTranslator
```

## Roadmap （扩展方向）

1. **JSON 解析器集成** — 引入 Jackson 或 Gson，把请求/响应结构化为强类型 `record`，替代手拼 JSON
2. **多引擎适配器模式** — 定义公共接口 `ITranslator`，实现 DeepL / OpenAI / Google 等多个适配器，业务层统一调用、底层可随时切换供应商
3. **本地缓存** — 用 Caffeine / Guava Cache 对高频重复文本做二级缓存，减少重复请求和 API 费用

## Files

- `ModernTranslator.java` — the whole client, a single self-contained class
