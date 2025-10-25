package br.com.jprangel.vaccination_api.model.enuns;

public enum DoseType {
  PRIMEIRA_DOSE("1ª Dose"),
  SEGUNDA_DOSE("2ª Dose"),
  TERCEIRA_DOSE("3ª Dose"),
  DOSE_UNICA("Dose Única"),
  REFORCO("Reforço"),
  PRIMEIRO_REFORCO("1º Reforço"),
  SEGUNDO_REFORCO("2º Reforço");

  private final String descricao;

  DoseType(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }
}
