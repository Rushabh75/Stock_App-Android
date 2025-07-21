function ChartRecommend(chartData) {
    const period = chartData.map(data => data.period);
    Highcharts.chart('container_recommendation', { // Added comma after 'container_recommendation'
        chart: {
            type: 'column',
            backgroundColor: 'white',
            marginBottom: 100
        },
        title: {
            text: 'Recommendation Trends',
            align: 'center'
        },
        xAxis: {
            categories: period,
        },
        yAxis: {
            min: 0,
            title: {
                text: '#Analysis'
            }
        },
        legend: {
            align: 'center',
            verticalAlign: 'bottom',
            y: -10,
            floating: true,
            backgroundColor: 'white',
            borderWidth: 1,
            shadow: false
        },
        tooltip: {
            headerFormat: '<b>{point.x}</b><br/>',
            pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
        },
        plotOptions: {
            column: {
                stacking: 'normal',
                dataLabels: {
                    enabled: true
                }
            }
        },
        series: [{
            type: 'column', // Explicitly declare the series type here
            name: 'Strong Buy',
            data: chartData.map(data => data.strongBuy),
            color: '#1a6334'
        }, {
            type: 'column',
            name: 'Buy',
            data: chartData.map(data => data.buy),
            color: '#25af51'
        }, {
            type: 'column',
            name: 'Hold',
            data: chartData.map(data => data.hold),
            color: '#b17e29'
        }, {
            type: 'column',
            name: 'Sell',
            data: chartData.map(data => data.sell),
            color: '#f15053'
        }, {
            type: 'column',
            name: 'Strong Sell',
            data: chartData.map(data => data.strongSell),
            color: '#752b2c'
        }]
    });
}
