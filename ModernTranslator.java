import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ModernTranslator {

    private final HttpClient httpClient;
    private final String apiKey;
    private final String apiEndpoint;

    /**
     * 构造函数
     * @param apiKey 翻译服务 API Key (如 DeepL 或大模型 API Key)
     * @param apiEndpoint 翻译 API 网关地址
     */
    public ModernTranslator(String apiKey, String apiEndpoint) {
        this.apiKey = apiKey;
        this.apiEndpoint = apiEndpoint;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * 同步翻译接口
     * @param text 待翻译文本
     * @param targetLang 目标语言代码 (如 "EN" 代表英语, "ZH" 代表中文)
     * @return 翻译后的文本或原始响应
     */
    public String translateSync(String text, String targetLang) throws Exception {
        String jsonPayload = buildJsonPayload(text, targetLang);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseTranslationResult(response.body());
        } else {
            throw new RuntimeException("翻译请求失败 [状态码: " + response.statusCode() + "]: " + response.body());
        }
    }

    /**
     * 异步翻译接口（适用于高并发或不阻塞主线程的场景）
     */
    public CompletableFuture<String> translateAsync(String text, String targetLang) {
        String jsonPayload = buildJsonPayload(text, targetLang);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return parseTranslationResult(response.body());
                    } else {
                        throw new RuntimeException("异步翻译失败: " + response.statusCode());
                    }
                });
    }

    /**
     * 组装请求 JSON 报文（实际项目中可引入 Jackson 或 Gson 处理）
     */
    private String buildJsonPayload(String text, String targetLang) {
        // 简单转义双引号，防止 JSON 报文被破坏
        String escapedText = text.replace("\"", "\\\"");
        return String.format("{\"text\": \"%s\", \"target_lang\": \"%s\"}", escapedText, targetLang);
    }

    /**
     * 解析响应结果（示例：提取返回的文本内容）
     */
    private String parseTranslationResult(String responseBody) {
        // 实际开发中可使用 Jackson/Gson 映射为实体类
        // 此处返回原始 JSON 或做简单字符串截取
        return responseBody;
    }

    // ==========================================
    // 测试运行入口
    // ==========================================
    public static void main(String[] args) {
        // 示例配置（需替换为真实的 API Key 和端点）
        String apiKey = "your_api_key_here";
        String endpoint = "https://api.deepL.com/v2/translate"; 

        ModernTranslator translator = new ModernTranslator(apiKey, endpoint);

        try {
            System.out.println("正在发起翻译请求...");
            // 同步调用测试
            // String result = translator.translateSync("Hello, Java developer!", "ZH");
            // System.out.println("翻译结果: " + result);
            
            // 异步调用测试
            translator.translateAsync("Advanced software engineering.", "ZH")
                    .thenAccept(res -> System.out.println("异步翻译成功: " + res))
                    .join(); // 阻塞等待异步完成以便观察输出

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
