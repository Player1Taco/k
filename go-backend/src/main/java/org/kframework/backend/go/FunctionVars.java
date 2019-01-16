package org.kframework.backend.go;

class FunctionVars {

    private static final String EVAL_ARG_NAME = "c";
    private final String[] varNames;

    public FunctionVars(int arity) {
        varNames = new String[arity];
        if (arity == 0) {
            // nothing
        } else if (arity == 1) {
            varNames[0] = EVAL_ARG_NAME;
        } else {
            for (int i = 0; i < arity; i++) {
                varNames[i] = EVAL_ARG_NAME + (i + 1);
            }
        }
    }

    public int arity() {
        return varNames.length;
    }

    public String varName(int i) {
        return varNames[i];
    }

    public String parameterDeclaration() {
        if (varNames.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String v : varNames) {
            sb.append(v);
            sb.append(" K, ");
        }
        return sb.toString();
    }

    public String callParameters() {
        if (varNames.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String v : varNames) {
            sb.append(v);
            sb.append(", ");
        }
        return sb.toString();
    }

}