package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 网页内容获取控制器
 */
@RestController
@RequestMapping("/api/web")
public class WebContentController {

    /**
     * 获取网页的英文文本内容
     * @param url 网页 URL
     * @return 提取的英文文本
     */
    @GetMapping("/content")
    public ApiResponse<String> getWebContent(@RequestParam String url) {
        try {
            // 验证 URL
            if (url == null || url.trim().isEmpty()) {
                return ApiResponse.error(400, "URL 不能为空");
            }

            // 确保 URL 以 http:// 或 https:// 开头
            String normalizedUrl = url.trim();
            if (!normalizedUrl.startsWith("http://") && !normalizedUrl.startsWith("https://")) {
                normalizedUrl = "https://" + normalizedUrl;
            }

            // 使用 Jsoup 获取网页内容
            Document doc = Jsoup.connect(normalizedUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // 提取文本内容
            String text = extractEnglishText(doc);

            if (text == null || text.trim().isEmpty()) {
                return ApiResponse.error(404, "未能提取到文本内容");
            }

            return ApiResponse.success("获取成功", text);
        } catch (IOException e) {
            return ApiResponse.error("获取网页内容失败：" + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("处理失败：" + e.getMessage());
        }
    }

    /**
     * 从 HTML 文档中提取英文文本
     * 优先提取主要内容区域，过滤掉导航、脚本等
     */
    private String extractEnglishText(Document doc) {
        StringBuilder text = new StringBuilder();

        // 移除脚本和样式标签
        doc.select("script, style, noscript, nav, header, footer, aside").remove();

        // 尝试找到主要内容区域
        // 常见的文章内容选择器，包括 pre 标签（用于保留格式的文本）
        Elements mainContent = doc.select("main, article, .content, #content, .post, .article, .entry-content, .text, pre, body");

        if (mainContent.isEmpty()) {
            // 如果没有找到特定内容区域，使用 body
            mainContent = doc.select("body");
        }

        // 提取文本，保留换行格式
        for (Element element : mainContent) {
            String elementText;
            // 如果是 pre 标签，保留原始格式
            if (element.tagName().equals("pre")) {
                elementText = element.text();
            } else {
                // 对于其他元素，尝试保留段落结构
                elementText = element.text();
            }
            
            if (elementText != null && !elementText.trim().isEmpty()) {
                // 过滤掉主要是非英文的内容
                String filteredText = filterEnglishText(elementText);
                if (filteredText != null && !filteredText.trim().isEmpty()) {
                    text.append(filteredText).append("\n\n");
                }
            }
        }

        // 如果主要内容区域没有提取到足够的文本，尝试从 body 提取
        if (text.length() < 100) {
            String bodyText = doc.body().text();
            String filteredText = filterEnglishText(bodyText);
            if (filteredText != null && !filteredText.trim().isEmpty()) {
                text = new StringBuilder(filteredText);
            }
        }

        // 清理多余的空白行，但保留段落之间的空行
        String result = text.toString().trim();
        result = result.replaceAll("\n{4,}", "\n\n\n"); // 最多保留3个连续换行
        
        return result;
    }

    /**
     * 过滤文本，保留主要是英文的内容
     */
    private String filterEnglishText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        // 按行分割
        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();

        // 英文单词的正则表达式（至少包含一个字母）
        Pattern englishPattern = Pattern.compile(".*[a-zA-Z].*");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                // 保留空行以维持段落结构
                result.append("\n");
                continue;
            }

            // 检查是否包含英文字母
            if (englishPattern.matcher(line).matches()) {
                // 计算英文字符比例
                int englishCharCount = 0;
                int totalCharCount = 0;

                for (char c : line.toCharArray()) {
                    if (Character.isLetter(c) || Character.isWhitespace(c) || 
                        c == '.' || c == ',' || c == '!' || c == '?' || c == ':' || 
                        c == ';' || c == '-' || c == '\'' || c == '"' || c == '(' || 
                        c == ')' || c == '[' || c == ']' || c == '{' || c == '}' ||
                        c == '_' || c == '/' || c == '\\') {
                        if (Character.isLetter(c)) {
                            englishCharCount++;
                        }
                        totalCharCount++;
                    } else if (!Character.isDigit(c)) {
                        totalCharCount++;
                    }
                }

                // 如果英文字符比例超过 20%，保留这一行（降低阈值以保留更多内容）
                if (totalCharCount > 0 && (double) englishCharCount / totalCharCount > 0.2) {
                    result.append(line).append("\n");
                }
            }
        }

        return result.toString().trim();
    }
}

