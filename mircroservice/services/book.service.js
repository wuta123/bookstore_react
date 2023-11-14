const { MoleculerClientError } = require("moleculer").Errors;
const DbService = require("moleculer-db");
const SqlAdapter = require("moleculer-db-adapter-sequelize");
const Sequelize = require("sequelize");
const cors = require("cors");

module.exports = {
    name: "book",
    mixins: [DbService],
    adapter: new SqlAdapter("postgres://postgres:password@localhost:5432/booklist"),
    model: {
        name: "book",
        define: {
            book_id: {
                //需要显式声明主键，因为默认的主键为id，不显示声明会导致错误
                type: Sequelize.UUID,
                primaryKey: true
            },
            title: Sequelize.STRING,
            price: Sequelize.STRING,
            description: Sequelize.STRING,
            author: Sequelize.STRING,
            type: Sequelize.STRING,
            image: Sequelize.STRING,
            remain: Sequelize.INTEGER,
            sold: Sequelize.INTEGER
        },
        options: {
            modelName: "book",
            tableName: "book",
            freezeTableName: true,
            timestamps: false,
        }
    },
    actions: {

        /*
        * GET /api/book/author/:title
        - 参数：pathvariable: title (书名)
        - 返回格式: if(success) 返回 {author: 作者名}
        * if(fail) 返回 {author: "您查找的书名有误或不存在！请输入正确的书名"}
        - 功能：通过书名返回作者，或者返回报错信息
        * */
        getAuthorByTitle: {
            rest: "GET /author/:title",
            params: {
                title: {type: "string"}
            },
            async handler(ctx) {
                const bookinfo = await this.adapter.find({
                    query: {title: ctx.params.title}
                })
                console.log(bookinfo);
                if(bookinfo[0] && bookinfo[0].author){
                    return {author: bookinfo[0].author};
                }
                else
                    return {author: "您查找的书名有误或不存在！请输入正确的书名."}
            }
        }
    }
};

