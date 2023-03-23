# 项目：谷粒学院

该项目是一个运行在PC端的线上教育系统，包含后台管理和用户端。本手册介绍了项目的详细信息和技术栈。

## 项目描述

该项目是一个线上教育系统，提供课程购买、在线学习、数据分析等功能。该系统包含后台管理和用户端，负责模块包括用户端(注册发送手机短信验证码、登录、浏览和收藏、我的订单等)及后台管理模块（用户管理、课程管理、讲师管理、数据分析），并且实现了swagger测试。

## 技术栈

主要采用以下技术：

- SpringBoot
- Mybatis-plus
- Redis
- Jwt
- Echarts
- Vue
- Element-UI

## 亮点

1. 认证通过，使用JWT生成token,实现用户令牌登录,设置token过期时间。
2. 使用Redis缓存，实现接口防刷。
3. 使用Echarts实现各业务饼图数据分析。
4. 使用Redis实现登录、验证、缓存功能。

## 环境要求

后端环境：

- JDK8
- MySQL5.7
- SpringBoot2
- SpringCloud2.2.0
- Redis6.0

前端环境：

- Node.js>=10
- Vue2
- npm>=6

## 贡献者

该项目由sq9960开发，开发时间为 2022 年 12 月至 2023 年 2 月。

## 安装和部署

1. 克隆代码到本地
2. 配置数据库信息
3. 配置 Redis 信息
4. 运行后台服务
5. 运行前端服务

具体操作请看对应文档。

## 鸣谢

感谢所有参与该项目的开发者和贡献者。
