var=$(cat  shipins.log  | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" | grep "保存完成" | wc | awk '{print $1}')
echo "$(date  -d yesterday '+%Y-%m-%d'):视频搜索:$var"
var=$(cat  baokans.log  | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" | grep "保存完成" | wc | awk '{print $1}')
echo "$(date  -d yesterday '+%Y-%m-%d'):报刊搜索:$var"
var=$(cat  bbss.log  | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" | grep "保存完成" | wc | awk '{print $1}')
echo "$(date  -d yesterday '+%Y-%m-%d'):论坛搜索:$var"
var=$(cat  clients.log  | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" | grep "保存完成" | wc | awk '{print $1}')
echo "$(date  -d yesterday '+%Y-%m-%d'):客户端搜索:$var"
var=$(cat  newss.log  | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" | grep "保存完成" | wc | awk '{print $1}')
echo "$(date  -d yesterday '+%Y-%m-%d'):新闻搜索:$var"
var=$(cat  waimeis.log  | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" | grep "保存完成" | wc | awk '{print $1}')
echo "$(date  -d yesterday '+%Y-%m-%d'):外媒搜索:$var"
var=$(cat  weixins.log  | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" | grep "保存完成" | wc | awk '{print $1}')
echo "$(date  -d yesterday '+%Y-%m-%d'):微信搜索:$var"
var=$(cat  zhengwus.log  | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" | grep "保存完成" | wc | awk '{print $1}')
echo "$(date  -d yesterday '+%Y-%m-%d'):政务搜索:$var"
var=$(cat  blogs.log  | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" | grep "保存完成" | wc | awk '{print $1}')
echo "$(date  -d yesterday '+%Y-%m-%d'):博客搜索:$var"
var=$(cat  weibos.log | grep "\[INFO \]\[$(date -d yesterday '+%Y-%m-%d')" |grep "所有新数据已保存。" | awk -F "。"  '{sum += $2} END {print sum}')
echo "$(date  -d yesterday '+%Y-%m-%d'):微博搜索:$var"