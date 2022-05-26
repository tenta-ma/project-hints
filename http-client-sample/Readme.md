## java.net.httpclientのサンプル

RestTemlateは非同期ができない(Asyncがあるけど根本的に別クラスを使う)
WebClientは非同期しかできない、のはいいけど戻り値がMono<T>, Flux<T>なので理解までに時間がかかるかも

java.net.httpclient はjava.netiveだからspringは不要、、、だが
getのquery parameter作るサンプル面倒だったため
org.springframework.web.util.UriComponentsBuilder
を利用するためspring-webでプロジェクト作成