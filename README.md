# NontageLib

## 繁體中文

### 介紹

NontageLib 是一個為 Bukkit/Paper 插件開發設計的實用工具庫。它既是一個獨立的插件，也是一個可以被其他插件依賴的函式庫。提供了多種工具類和功能，幫助開發者更快速、更高效地開發
Minecraft 伺服器插件。

### 功能

- **自定義物品建造器**: 簡化物品創建與自定義過程
- **自定義介面系統**: 輕鬆創建與管理交互式介面
- **命令框架**: 自動註冊和管理命令
- **事件監聽器註冊**: 通過註解自動註冊事件監聽器
- **反射工具**: 簡化反射操作
- **世界工具**: 輔助世界管理

### 使用方法

#### 自定義介面

```java
// 創建一個有27格的介面，標題為"測試介面"
InventoryBuilder inv = new InventoryBuilder(27, "測試介面");

// 設置物品並添加點擊事件
inv.setItem(
    new ItemBuilder(Material.DIAMOND)
        .setName("§b點擊我!")
        .setLore("§7點擊獲得鑽石")
        .build(),
event -> {
Player player = event.getPlayer();
        player.getInventory().addItem(new ItemStack(Material.DIAMOND));
        player.sendMessage("§a你獲得了一個鑽石!");
    },
            13
            );

// 設置關閉事件
            inv.setCloseEvent(event -> {
        event.getPlayer().sendMessage("§c你關閉了介面!");
});

// 打開介面
        player.openInventory(inv.getInventory());
```

#### 自定義物品

```java
ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
        .setName("§c神劍")
        .setLore("§7這是一把很強的劍", "§7它能秒殺大多數怪物")
        .enchant(Enchantment.DAMAGE_ALL, 10)
        .unBreak()
        .hideEnchant()
        .build();
```

#### 命令註冊

```java

@CommandInfo(name = "test", description = "測試命令", aliases = {"t"}, permission = "test.use")
public class TestCommand implements NontageCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage("§a這是一個測試命令!");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        if (args.length == 1) {
            return List.of("help", "info");
        }
        return List.of();
    }
}

// 在插件主類中註冊所有命令
NontageCommandLoader.

registerAll(this);
```

#### 自動註冊事件監聽器

```java

@AutoListener
public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§a歡迎來到伺服器!");
    }
}

// 在插件主類中註冊所有監聽器
ListenerRegister.

registerAll(this);
```

### 安裝

#### 作為插件使用

1. 下載最新的 NontageLib.jar 文件
2. 將它放置到你的伺服器的 plugins 資料夾中
3. 重新啟動伺服器

#### 作為依賴庫使用

將 NontageLib 添加到您的 Maven 或 Gradle 項目中。

Maven (使用 JitPack): [JitPack](https://jitpack.io/#SeanMud0319/NontageLib/1.0-SNAPSHOT)

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>com.github.SeanMud0319</groupId>
    <artifactId>NontageLib</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
</dependencies>
```

Gradle (使用 JitPack):

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.SeanMud0319:NontageLib:1.0-SNAPSHOT'
}
```

同時，請確保在你的 plugin.yml 中添加 NontageLib 作為依賴：

```yaml
depend: [ NontageLib ]
```

## English

### Introduction

NontageLib is a utility library designed for Bukkit/Paper plugin development. It functions both as a standalone plugin
and as a library that can be depended on by other plugins. It provides a variety of utility classes and features to help
developers create Minecraft server plugins more quickly and efficiently.

### Features

- **Custom Item Builder**: Simplifies item creation and customization
- **Custom Inventory System**: Easily create and manage interactive inventories
- **Command Framework**: Automatically register and manage commands
- **Listener Registration**: Auto-register event listeners with annotations
- **Reflection Utilities**: Simplify reflection operations
- **World Utilities**: Assist with world management

### Usage

#### Custom Inventories

```java
// Create an inventory with 27 slots and the title "Test Inventory"
InventoryBuilder inv = new InventoryBuilder(27, "Test Inventory");

// Set an item with a click event
inv.setItem(
    new ItemBuilder(Material.DIAMOND)
        .setName("§bClick me!")
        .setLore("§7Click to get a diamond")
        .build(),
event -> {
Player player = event.getPlayer();
        player.getInventory().addItem(new ItemStack(Material.DIAMOND));
        player.sendMessage("§aYou received a diamond!");
    },
            13
            );

// Set a close event
            inv.setCloseEvent(event -> {
        event.getPlayer().sendMessage("§cYou closed the inventory!");
});

// Open the inventory
        player.openInventory(inv.getInventory());
```

#### Custom Items

```java
ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
        .setName("§cGod Sword")
        .setLore("§7This is a powerful sword", "§7It can one-hit most mobs")
        .enchant(Enchantment.DAMAGE_ALL, 10)
        .unBreak()
        .hideEnchant()
        .build();
```

#### Command Registration

```java

@CommandInfo(name = "test", description = "Test command", aliases = {"t"}, permission = "test.use")
public class TestCommand implements NontageCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage("§aThis is a test command!");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        if (args.length == 1) {
            return List.of("help", "info");
        }
        return List.of();
    }
}

// Register all commands in the main plugin class
NontageCommandLoader.

registerAll(this);
```

#### Auto-register Event Listeners

```java

@AutoListener
public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§aWelcome to the server!");
    }
}

// Register all listeners in the main plugin class
ListenerRegister.

registerAll(this);
```

### Installation

#### As a Plugin

1. Download the latest NontageLib.jar file
2. Place it in your server's plugins folder
3. Restart your server

#### As a Dependency

Add NontageLib to your Maven or Gradle project.

Maven (using JitPack): [JitPack](https://jitpack.io/#SeanMud0319/NontageLib/1.0-SNAPSHOT)

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>com.github.SeanMud0319</groupId>
    <artifactId>NontageLib</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
</dependencies>
```

Gradle (using JitPack):

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.SeanMud0319:NontageLib:1.0-SNAPSHOT'
}
```

Also, make sure to add NontageLib as a dependency in your plugin.yml:

```yaml
depend: [ NontageLib ]
```