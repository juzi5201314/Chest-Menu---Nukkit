# Chest-Menu---Nukkit
this is a mcpe server plugin

一个nk版的箱子菜单，因为我还是萌新(反正萌就对了)，所以写的貌似有点难配置#(滑稽)

/menu add [id]

新建一个菜单。

/menu send [id]

给自己显示一个菜单。

/menu set [id]

进入设置模式，可自由从菜单取出/放入物品。

/menu item new [名字]

设置手持物品为菜单物品

/menu item set [属性] [数值]

(close  true/false  输入true点击按钮会关闭菜单)

(give  物品id:物品特殊值  给予玩家物品，重复输入可添加多个物品)

(del  物品id:物品特殊值  删除玩家的物品，重复输入可添加多个物品)

(add  数字  给玩家money)

(remove  数字  减少玩家money)

(cmd  指令  用.代替空格，{p}代替执行的玩家名字，指令前面加{op}执行op指令，如{op}say.{p}。重复输入可添加多个指令)。

/menu reload

重载菜单数据和配置文件

配置文件config可改手持什么物品点击打开菜单。

进服会给玩家一张地图，手持地图潜行也会打开菜单，可在配置文件关闭打开，打开什么菜单也要设置，填菜单id。

手持地图潜行打开菜单有几率失败，因为nk的物品存多了nbt就炸

要改地图上的图片只要更改插件目录下的Menu.png然后重启服务器，记得改名成Menu.png

money使用@him188的money插件<a href = 'http://www.juzi5201314.win/end_poem/end.html'></a>
