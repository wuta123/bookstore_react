var program = require('commander')
var fs = require('fs')

program
    .version('0.0.1')
    .parse(process.argv)

var modules = program.args
if (modules.length === 0) {
    modules = fs.readdirSync(__dirname + "\\services").filter(file => file.search(/\.js$/) !== -1 && file !== 'test.js' && file !== 'index.js')
}
console.log('modules ==>', modules)

modules.map(module => require(`${__dirname + "\\services"}/${module}`))
