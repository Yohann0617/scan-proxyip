# 第一阶段：使用Maven构建jar
FROM maven:3.8.4-openjdk-11 AS builder

# 设置工作目录
WORKDIR /app

# 复制Maven项目描述文件和依赖清单
COPY pom.xml .
COPY src ./src

# 执行Maven构建并将构建的jar文件复制到指定目录
RUN mvn clean package -DskipTests \
    && cp target/select-1.0.3.jar /app/select-1.0.3.jar

# 支持AMD、ARM两种架构的镜像
FROM dragonwell-registry.cn-hangzhou.cr.aliyuncs.com/dragonwell/dragonwell:8-centos

# 安装依赖包
RUN yum install -y bind-utils curl epel-release \
    && yum install -y jq \
    && yum clean all \
    && ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo Asia/Shanghai > /etc/timezone

# 设置工作目录
WORKDIR /app

# 从第一阶段复制构建的jar文件
COPY --from=builder /app/select-1.0.3.jar .

# 拷贝数据库文件
COPY scan.db .

# 暴露应用的端口
EXPOSE 8017

# 定义启动命令
CMD ["java", "-jar", "select-1.0.3.jar"]
