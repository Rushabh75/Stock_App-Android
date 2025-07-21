function ChartEarn(chartData){
Highcharts.chart('container_eps', {
        chart: {
            type: 'spline'
        },
        title: {
            text: 'Historical EPS Surprises'
        },
        xAxis: {
            categories: chartData.map(data => data.period)
        },
        yAxis: {
            title: {
                text: 'Quarterly EPS'
            }
        },
        tooltip: {
            shared: true,
            pointFormat: '{series.name}: <b>{point.y}</b><br/>',
            valueSuffix: ' EPS'
        },
        plotOptions: {
            spline: {
                marker: {
                    enabled: true
                }
            }
        },
        series: [{
            name: 'Actual',
            data: chartData.map(data => data.actual)
        }, {
            name: 'Estimate',
            data: chartData.map(data => data.estimate)
        }]
    });
}