package domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EspecieCompletaResponse {
    @JsonProperty("status_validacao")
    private String statusValidacao;

    private String nomeCientifico;
    private String nomePopular;
    private String descricao;

    private TaxonDTO taxon;
    private GeografiaDTO geografia;
    private EcologiaDTO ecologia;

    public EspecieCompletaResponse() {
    }

    public EspecieCompletaResponse(String statusValidacao, TaxonDTO taxon, GeografiaDTO geografia, EcologiaDTO ecologia) {
        this.statusValidacao = statusValidacao;
        this.taxon = taxon;
        this.geografia = geografia;
        this.ecologia = ecologia;
    }

    // Getters e Setters
    public String getStatusValidacao() {
        return statusValidacao;
    }

    public void setStatusValidacao(String statusValidacao) {
        this.statusValidacao = statusValidacao;
    }

    public String getNomeCientifico() {
        return nomeCientifico;
    }

    public void setNomeCientifico(String nomeCientifico) {
        this.nomeCientifico = nomeCientifico;
    }

    public String getNomePopular() {
        return nomePopular;
    }

    public void setNomePopular(String nomePopular) {
        this.nomePopular = nomePopular;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TaxonDTO getTaxon() {
        return taxon;
    }

    public void setTaxon(TaxonDTO taxon) {
        this.taxon = taxon;
    }

    public GeografiaDTO getGeografia() {
        return geografia;
    }

    public void setGeografia(GeografiaDTO geografia) {
        this.geografia = geografia;
    }

    public EcologiaDTO getEcologia() {
        return ecologia;
    }

    public void setEcologia(EcologiaDTO ecologia) {
        this.ecologia = ecologia;
    }

    public static class EcologiaDTO {

        @JsonProperty("status_conservacao")
        private String statusConservacao;

        private String origem;

        @JsonProperty("importancia_ecologica")
        private String importanciaEcologica;

        // Construtores
        public EcologiaDTO() {
        }

        // Getters e Setters
        public String getStatusConservacao() {
            return statusConservacao;
        }

        public void setStatusConservacao(String statusConservacao) {
            this.statusConservacao = statusConservacao;
        }

        public String getOrigem() {
            return origem;
        }

        public void setOrigem(String origem) {
            this.origem = origem;
        }

        public String getImportanciaEcologica() {
            return importanciaEcologica;
        }

        public void setImportanciaEcologica(String importanciaEcologica) {
            this.importanciaEcologica = importanciaEcologica;
        }
    }

    public static class GeografiaDTO {

        private String municipio;
        private String uf;
        private String bioma;

        // Construtores
        public GeografiaDTO() {
        }

        // Getters e Setters
        public String getMunicipio() {
            return municipio;
        }

        public void setMunicipio(String municipio) {
            this.municipio = municipio;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }

        public String getBioma() {
            return bioma;
        }

        public void setBioma(String bioma) {
            this.bioma = bioma;
        }
    }

    public static class TaxonDTO {

        private String reino;
        private String divisao;
        private String familia;
        private String genero;
        private String especie;
        private String autor;

        @JsonProperty("nome_comum_confirmado")
        private String nomeComumConfirmado;

        // Construtores
        public TaxonDTO() {
        }

        // Getters e Setters
        public String getReino() {
            return reino;
        }

        public void setReino(String reino) {
            this.reino = reino;
        }

        public String getDivisao() {
            return divisao;
        }

        public void setDivisao(String divisao) {
            this.divisao = divisao;
        }

        public String getFamilia() {
            return familia;
        }

        public void setFamilia(String familia) {
            this.familia = familia;
        }

        public String getGenero() {
            return genero;
        }

        public void setGenero(String genero) {
            this.genero = genero;
        }

        public String getEspecie() {
            return especie;
        }

        public void setEspecie(String especie) {
            this.especie = especie;
        }

        public String getAutor() {
            return autor;
        }

        public void setAutor(String autor) {
            this.autor = autor;
        }

        public String getNomeComumConfirmado() {
            return nomeComumConfirmado;
        }

        public void setNomeComumConfirmado(String nomeComumConfirmado) {
            this.nomeComumConfirmado = nomeComumConfirmado;
        }
    }

}

