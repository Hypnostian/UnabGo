module.exports = {
  root: true,
  parser: "@typescript-eslint/parser",
  parserOptions: {
    project: ["tsconfig.json"],
    sourceType: "module",
  },
  env: {
    node: true,
  },
  rules: {
    // Se desactivan reglas para evitar errores de formato durante deploy
  }
};
