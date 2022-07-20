package ioNetty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class NettyHttpRequest {

    public static void main(String[] args){

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpClientCodec());
                pipeline.addLast(new NettyHttpResponse());
            }
        });

        String content = "{\"Name\": \"홍길동\",\"amt\": \"10000\"}";

        try {
            URI uri = new URI("http://192.168.124.250:1381/retrieve");
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
            request.headers().set(HttpHeaderNames.HOST, uri.getHost());
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

            ByteBuf byteBuf = Unpooled.copiedBuffer(content, StandardCharsets.UTF_8);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            request.content().writeBytes(byteBuf);

            Channel ch = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
            ch.writeAndFlush(request);
            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}