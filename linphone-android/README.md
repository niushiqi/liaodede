# Linphone Android Module

## 概述

linphone-android模块是聊得得项目中的一个核心组件，专门负责处理语音和视频通话功能。该模块基于著名的开源SIP客户端Linphone开发，提供了完整的VoIP（Voice over IP）通信解决方案。

## 功能特性

### 1. 语音通话
- 高质量音频通话
- 支持多种音频编解码器
- 回声消除和噪声抑制
- 自动增益控制

### 2. 视频通话
- 高清视频通话支持
- 多种视频编解码器
- 自适应带宽管理
- 视频渲染优化

### 3. 即时消息
- SIP MESSAGE消息支持
- 文本聊天功能
- 消息状态跟踪

### 4. 联系人管理
- 本地联系人同步
- 在线状态显示
- 联系人搜索功能

### 5. 呼叫管理
- 呼叫历史记录
- 通话录音功能
- 呼叫转移和保持

## 技术架构

### 核心组件
1. **LinphoneCore**: 核心引擎，处理SIP协议和媒体流
2. **Mediastreamer2**: 媒体处理库，负责音频和视频处理
3. **oRTP**: RTP协议实现，用于实时媒体传输
4. **eXosip**: SIP协议栈实现

### 支持的协议
- SIP (Session Initiation Protocol)
- RTP/RTCP (Real-time Transport Protocol)
- STUN/ICE (NAT穿越)
- TLS (安全传输)

### 编解码器支持
- 音频: G.711, G.722, G.729, Opus, Speex
- 视频: H.263, H.264, VP8, VP9

## 目录结构

```
linphone-android/
├── src/
│   ├── main/
│   │   ├── java/org/linphone/      # Java源代码
│   │   │   ├── core/               # 核心功能类
│   │   │   ├── ui/                 # 用户界面组件
│   │   │   ├── service/            # 后台服务
│   │   │   └── receiver/           # 广播接收器
│   │   ├── res/                    # 资源文件
│   │   └── AndroidManifest.xml     # 模块配置文件
├── libs/                           # 本地依赖库
├── build.gradle                    # 构建配置文件
└── proguard-rules.pro              # 代码混淆规则
```

## 集成方式

### 依赖配置
在主应用的build.gradle文件中添加对linphone-android模块的依赖：

```gradle
dependencies {
    implementation project(':linphone-android')
}
```

### 权限要求
模块需要以下Android权限：
- INTERNET - 网络访问
- RECORD_AUDIO - 音频录制
- CAMERA - 摄像头访问
- READ_CONTACTS - 读取联系人
- WRITE_CONTACTS - 写入联系人

## 使用方法

### 初始化
```java
LinphoneManager.createAndStart(Context context);
```

### 发起呼叫
```java
LinphoneManager.getInstance().newOutgoingCall(String address, String displayName);
```

### 接听来电
```java
LinphoneManager.getInstance().acceptCall(LinphoneCall call);
```

## 配置选项

### 网络设置
- STUN服务器配置
- ICE候选地址收集
- 媒体加密设置

### 音视频设置
- 编解码器优先级
- 带宽限制
- QoS参数配置

## 注意事项

1. 该模块需要较大量的内存和处理能力
2. 在后台运行时需要特殊处理以保持连接
3. 需要处理各种网络环境下的连接稳定性
4. 音视频权限需要在运行时申请

## 版本信息
当前版本: 3.3.2 (版本代码: 3320)