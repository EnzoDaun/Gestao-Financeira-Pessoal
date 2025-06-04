// src/main/java/org/example/model/dao/ICategoriaDAO.java
package org.example.model.dao;

import org.example.model.Categoria;

import java.util.List;

public interface ICategoriaDAO {
    void save(Categoria entity);
    void update(Categoria entity);
    void delete(Integer id);
    Categoria findById(Integer id);
    List<Categoria> findAll();

    /**
     * Tenta remover a categoria de ID igual a `id` **somente se** ela pertencer ao usuário de ID `userId`.
     * @param id     ID da categoria a remover.
     * @param userId ID do usuário que supostamente a “possui”.
     * @return número de linhas afetadas (0 se não existia OU se pertencia a outro usuário; 1 se removida com sucesso).
     */
    int deleteByIdAndUser(Integer id, Integer userId);
}
