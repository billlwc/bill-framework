package com.bill.test.code;


import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

public class CodeGenerator {
    public static void main(String[] args) {
        String basePackage = "com.bill";
        String moduleName = "test";
        String[] tableName = {"store"};

        FastAutoGenerator.create("jdbc:postgresql://117.72.94.34:5432/cc", "root", "4396")
                .globalConfig(builder -> {
                    builder.author("bill")
                            .enableSpringdoc() // 用 @Schema 而不是 ApiModel
                            .dateType(DateType.TIME_PACK)
                            .outputDir(System.getProperty("user.dir") + "/src/main/java");
                })
                .packageConfig(builder -> {
                    // 包路径 = com.bill.test
                    builder.parent(basePackage).moduleName(moduleName);
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tableName)
                            // ---------- Entity ----------
                            .entityBuilder()
                            .superClass("bill.framework.web.reply.BaseEntity") // ✅ 完整限定名
                            .enableLombok()
                            .enableChainModel()
                            .enableTableFieldAnnotation()
                            .addSuperEntityColumns("id", "create_time", "update_time") // 父类公共字段
                            .enableFileOverride()
                            // ---------- Service ----------
                            .serviceBuilder()
                            .formatServiceImplFileName("%sService") // ✅ 生成 xxxService (实际上是 Impl)
                            .enableFileOverride()
                            // ---------- Mapper ----------
                            .mapperBuilder()
                            .enableMapperAnnotation() // ✅ Mapper 接口加 @Mapper
                            .enableFileOverride()
                            .build();
                })
                // 不生成 Controller
                .templateConfig(builder -> {
                    builder.controller(""); // 禁止生成 controller 模板
                })
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }


}

