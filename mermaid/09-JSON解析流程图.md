# JSON解析流程图

```mermaid
flowchart TD
    Start([开始解析JSON文件]) --> ReadFile[读取文件内容<br/>readAll]
    ReadFile --> FindArray[查找数组开始标记 '[']
    FindArray -->|未找到| ReturnEmpty[返回空列表]
    FindArray -->|找到| InitPos[初始化位置指针 pos = start + 1]
    
    InitPos --> LoopStart{pos < content.length?}
    
    LoopStart -->|否| ReturnResult[返回结果列表]
    LoopStart -->|是| FindObjStart[查找对象开始标记 '{']
    
    FindObjStart -->|未找到| ReturnResult
    FindObjStart -->|找到| FindMatchingBrace[调用findMatchingBrace<br/>查找匹配的结束大括号]
    
    FindMatchingBrace -->|未找到| ReturnResult
    FindMatchingBrace -->|找到| ExtractJson[提取对象JSON字符串<br/>substring objStart to objEnd+1]
    
    ExtractJson --> ParseObject[解析对象<br/>parseStudent/parseAdmin/parseCourt/parseBooking]
    
    ParseObject --> ExtractFields[使用正则表达式提取字段<br/>extractString/extractDouble/extractInt]
    
    ExtractFields --> CreateObject[创建对象实例<br/>new Student/Admin/Court/Booking]
    
    CreateObject --> AddToList[添加到结果列表]
    AddToList --> UpdatePos[更新位置指针<br/>pos = objEnd + 1]
    UpdatePos --> LoopStart
    
    ReturnResult --> End([结束])
    ReturnEmpty --> End
    
    subgraph "findMatchingBrace 算法"
        StartBrace[开始位置 start] --> InitCount[初始化计数器 count = 0]
        InitCount --> LoopChar{遍历字符 i from start}
        LoopChar -->|遇到 '{'| IncCount[count++]
        LoopChar -->|遇到 '}'| DecCount[count--]
        DecCount --> CheckZero{count == 0?}
        CheckZero -->|是| ReturnPos[返回位置 i]
        CheckZero -->|否| LoopChar
        IncCount --> LoopChar
        ReturnPos --> EndBrace[结束]
    end
    
    style Start fill:#e3f2fd
    style ReadFile fill:#fff3e0
    style FindMatchingBrace fill:#f3e5f5
    style ParseObject fill:#e8f5e9
    style ExtractFields fill:#fff9c4
    style CreateObject fill:#ffebee
    style End fill:#e3f2fd
```

