Esse repositório contém o post-mortem de um trabalho de faculdade, no qual o objetivo final era implementar um sistema de administração de usuários e solicitações para uma empresa. O resultado final está disposto acima, e segue, de maneira resumida, a seguinte lógica:

1. O projeto roda em um banco PostgreSQL;
2. Existe a presença de um SUPERUSER, de uso restrito da TI, com acessos ilimitados;
3. Os cargos são divididos em Diretor, Chefe e Funcionário;
4. Diretores não possuem setor, porém chefes e funcionários possuem, e o SUPERUSER possui seu próprio setor;
5. Diretores e chefes podem criar usuários, mas só podem excluir funcionários de seu próprio setor (não se aplica a diretores) e de cargo inferior ao seu;
6. Diretores e chefes podem abrir solicitações para a TI, e podem atribuir um funcionário da TI para resolver;
7. O chefe da TI pode reatribuir o responsável;
8. Apenas o chefe ou o funcionário responsável pela solicitação da TI podem movimentar ou concluir solicitações;
9. O SUPERUSER possui uma aba extra de logs de movimentação no banco de dados, onde é possível auditar os ocorridos;
10. Todo usuário possui login e senha;
11. Usuários novos são atribuídos ao setor público;
12. Usuários e setores podem ser desativados ou excluídos. Usuários desativados são impedidos de logar. Setores desativados não podem ser atribuídos. Usuários em setores desativados não perdem seus setores quando o setor é desativado. Quando um setor é excluído, todos os usuários são jogados para o setor público.
