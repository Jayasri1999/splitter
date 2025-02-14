package com.example.splitter.adapter;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Splitter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("activemq:splitter.in")
            .split().jsonpath("$.order.category.subcategories[0].items[*]")
            .process(exchange->{
            	if (exchange.getIn().getHeader("exitProcess") != null) {
                    exchange.getIn().setHeader("nextHop", "exit");
                } else {
                	exchange.getIn().setHeader("nextHop", "outBound");
        		}
            })
            .choice()
            .when(header("nextHop").isEqualTo("exit"))
                .to("activemq:exit.in")
    		.when(header("nextHop").isEqualTo("outbound"))
    			.to("activemq:outboud.in");
    }
}