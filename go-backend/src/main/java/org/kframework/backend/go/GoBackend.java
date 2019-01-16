// Copyright (c) 2015-2018 Runtime Verification, Inc. (RV-Match team). All Rights Reserved.
package org.kframework.backend.go;

import com.google.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.kframework.compile.Backend;
import org.kframework.definition.Definition;
import org.kframework.definition.Module;
import org.kframework.kompile.CompiledDefinition;
import org.kframework.kompile.Kompile;
import org.kframework.kompile.KompileOptions;
import org.kframework.main.GlobalOptions;
import org.kframework.utils.errorsystem.KEMException;
import org.kframework.utils.errorsystem.KExceptionManager;
import org.kframework.utils.file.FileUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class GoBackend implements Backend {

    private final KExceptionManager kem;
    private final FileUtil files;
    private final GlobalOptions globalOptions;
    private final KompileOptions kompileOptions;
    private final GoOptions options;
    private GoPackageNameManager packageNameManager;

    @Inject
    public GoBackend(KExceptionManager kem, FileUtil files, GlobalOptions globalOptions, KompileOptions kompileOptions, GoOptions options) {
        this.kem = kem;
        this.files = files;
        this.globalOptions = globalOptions;
        this.kompileOptions = kompileOptions;
        this.options = options;
    }

    @Override
    public void accept(CompiledDefinition compiledDefinition) {

        System.out.println("GoBackend.accept started.");

        String mainModule = kompileOptions.mainModule(files);
        //packageNameManager = new GoPackageNameManager(files,mainModule.toLowerCase() + "interpreter");
        packageNameManager = new GoPackageNameManager(files, "main");

        DefinitionToOcamlTempCopy ocamlDef = new DefinitionToOcamlTempCopy(kem, files, globalOptions, kompileOptions, options);
        DefinitionToGo def = new DefinitionToGo(kem, files, packageNameManager, globalOptions, kompileOptions, options);
        ocamlDef.initialize(compiledDefinition);
        def.initialize(compiledDefinition);

        // temporary, for convenience and comparison
        files.saveToKompiled("constants.ml", ocamlDef.constants());

        files.saveToKompiled("klabel.go", def.klabels());
        files.saveToKompiled("sort.go", def.sorts());
        files.saveToKompiled("fresh.go", def.freshDefinition());

        // temporary, for convenience and comparison
        try {
            files.saveToKompiled("realdef.ml", ocamlDef.definition());

            files.saveToKompiled("definition.go", def.definition());
            files.saveToKompiled("eval.go", def.evalDefinition());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // lexer, parser
            FileUtils.copyFile(files.resolveKBase("include/go/koreparser/stringutil.go"), files.resolveKompiled("koreparser/stringutil.go"));
            FileUtils.copyFile(files.resolveKBase("include/go/koreparser/model.go"), files.resolveKompiled("koreparser/model.go"));
            FileUtils.copyFile(files.resolveKBase("include/go/koreparser/korelex.go"), files.resolveKompiled("koreparser/korelex.go"));
            FileUtils.copyFile(files.resolveKBase("include/go/koreparser/koreparser.y"), files.resolveKompiled("koreparser/koreparser.y"));
            FileUtils.copyFile(files.resolveKBase("include/go/koreparser/gen.go"), files.resolveKompiled("koreparser/gen.go"));

            // interpreter
            packageNameManager.copyFileAndReplaceGoPackages(
                    files.resolveKBase("include/go/kmodel.go"), files.resolveKompiled("kmodel.go"));
            packageNameManager.copyFileAndReplaceGoPackages(
                    files.resolveKBase("include/go/init.go"), files.resolveKompiled("init.go"));
            packageNameManager.copyFileAndReplaceGoPackages(
                    files.resolveKBase("include/go/main.go"), files.resolveKompiled("main.go"));

            // builtin hook files
            for (String hookNamespace : GoBuiltin.HOOK_NAMESPACES) {
                String fileName = "hooks_" + hookNamespace.toLowerCase() + ".go";
                packageNameManager.copyFileAndReplaceGoPackages(
                        files.resolveKBase("include/go/hooks/" + fileName),
                        files.resolveKompiled(fileName));
            }

        } catch (IOException e) {
            throw KEMException.criticalError("Error copying go files: " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        try {
            ProcessBuilder pb = files.getProcessBuilder();
            int exit;
            exit = pb.command("go", "generate").directory(files.resolveKompiled("./koreparser")).inheritIO().start().waitFor();
            if (exit != 0) {
                throw KEMException.criticalError("go generate returned nonzero exit code: " + exit + "\nExamine output to see errors.");
            }
            if (!options.srcOnly) {
                exit = pb.command("go", "build").directory(files.resolveKompiled(".")).inheritIO().start().waitFor();
                if (exit != 0) {
                    throw KEMException.criticalError("go build returned nonzero exit code: " + exit + "\nExamine output to see errors.");
                }

                if (options.quickTest != null) {
                    String execCommand = "./" + files.getKompiledDirectoryName();
                    exit = pb.command(execCommand, options.quickTest).directory(files.resolveKompiled(".")).inheritIO().start().waitFor();
                    if (exit != 0) {
                        throw KEMException.criticalError("interpreter returned nonzero exit code: " + exit + "\nExamine output to see errors.");
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw KEMException.criticalError("Go process interrupted.", e);
        } catch (IOException e) {
            throw KEMException.criticalError("Error starting go build process: " + e.getMessage(), e);
        }


        System.out.println("GoBackend.accept completed successfully.");
    }

    @Override
    public Function<Definition, Definition> steps() {
        return Kompile.defaultSteps(kompileOptions, kem, files);
    }

    @Override
    public Function<Module, Module> specificationSteps(Definition ignored) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> excludedModuleTags() {
        return new HashSet<>(Arrays.asList("symbolic", "kore"));
    }
}