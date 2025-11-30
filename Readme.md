## 项目特点

### 1. **核心组件**
- **配置层** (`config/`): 不可变配置对象,Builder模式
- **转换层** (`converter/`): 编排转换流程
- **渲染层** (`renderer/`): PDF页面渲染
- **处理层** (`processor/`): 图像后处理(旋转、裁剪)
- **存储层** (`storage/`): 可扩展的存储策略

### 2. **灵活配置**
- 支持PNG/JPEG/JPG格式
- 彩色/灰度/黑白模式
- 三档DPI分辨率
- 页面范围选择
- 图像旋转
- 抗锯齿和渲染优化

### 3. **代码示例**

```java
ContextBase context = new ContextBase();
ConversionConfig config = ConversionConfig.builder()
        .inputDirectory(Paths.get("input"))
        .outputDirectory(Paths.get("output"))
        .imageFormat(ImageFormat.JPEG)
        .imageMode(ImageMode.COLOR)
        .resolution(Resolution.STANDARD)
        .pageRange(1, 10)
        .renderingConfig(RenderingConfig.builder()
                .enableAntiAliasing(true)
                .enableTextAntiAliasing(true)
                .enableFractionalMetrics(true)
                .build()
        )
        .build();
context.put("config", config);
Pdf2ImageConverter pdf2ImageConverter = Pdf2ImageConverter.createDefaultConverter();
pdf2ImageConverter.convert(context);
```