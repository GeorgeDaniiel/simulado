/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import modelo.Professor;

/**
 *
 * @author George
 */
public class ProfessorJpaController implements Serializable {

    public ProfessorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Professor professor) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(professor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Professor professor) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            professor = em.merge(professor);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = professor.getId();
                if (findProfessor(id) == null) {
                    throw new NonexistentEntityException("The professor with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Professor professor;
            try {
                professor = em.getReference(Professor.class, id);
                professor.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The professor with id " + id + " no longer exists.", enfe);
            }
            em.remove(professor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Professor> findProfessorEntities() {
        return findProfessorEntities(true, -1, -1);
    }

    public List<Professor> findProfessorEntities(int maxResults, int firstResult) {
        return findProfessorEntities(false, maxResults, firstResult);
    }

    private List<Professor> findProfessorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Professor as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Professor findProfessor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Professor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProfessorCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Professor as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
