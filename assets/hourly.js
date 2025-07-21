function plotHourlyChart(data, symbol) {
    // Process the data to the format Highcharts expects
    var ticker = symbol;
    var hchart = data.results.map(function(item) {
        return { price: item.c, time: item.t };
    });

    var hchartdata = hchart.map(function(point) {
        return [point.time, point.price];
    });

    // Determine the color of the series based on price increase or decrease
    var seriesColor = 'green'; // Default to green, you can adjust the logic to determine color
    if (hchartdata.length > 1) {
            var firstPrice = hchartdata[0][1]; // First price
            var lastPrice = hchartdata[hchartdata.length - 1][1]; // Last price

            if (lastPrice < firstPrice) {
                seriesColor = 'red'; // If the last price is less than the first, set color to red
            }
        }

    // Initialize Highcharts with the processed data
    Highcharts.chart('hourlyContainer', {
        chart: {
            type: 'line' // Set the chart type to line
        },
        title: {
            text: ticker + ' Hourly Stock Price' // Use the ticker to set the chart title
        },
        xAxis: {
            type: 'datetime', // Set the X-axis type to datetime
            title: {
                text: 'Time' // Add a title to the X-axis
            }
        },
        yAxis: {

        },
        series: [{
            name: 'Stock Price', // Add a name to the series
            data: hchartdata, // Map the data to [x, y] format
            marker: {
                enabled: false
            },
            color: seriesColor // Set the color of the series
        }]
    });
}
