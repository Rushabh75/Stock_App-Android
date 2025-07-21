function plotChart(data, symbol) {
            var ohlc = [], volume = [];
            var symbol = symbol;

            data.forEach(function (item) {
                var tempTime = new Date(item.t);
                var correctTime = tempTime.getTime();
                ohlc.push([
                    correctTime,
                    item.o,
                    item.h,
                    item.l,
                    item.c
                ]);
                volume.push([
                    correctTime,
                    item.v
                ]);
            });
             Highcharts.chart('historicalContainer',{

                                                   //   chart: {
                                                   //     type: 'candlestick',
                                                   //     height: '700px',
                                                   //     width:null,
                                                   //     backgroundColor: '#FAFAFA',
                                                   //     reflow: true,
                                                   // },

                                                   rangeSelector: {
                                                       enabled: true,
                                                       selected: 2,
                                                       buttons: [{
                                                           type: 'month',
                                                           count: 1,
                                                           text: '1m'
                                                       }, {
                                                           type: 'month',
                                                           count: 3,
                                                           text: '3m'
                                                       }, {
                                                           type: 'month',
                                                           count: 6,
                                                           text: '6m'
                                                       }, {
                                                           type: 'ytd',
                                                           text: 'YTD'
                                                       }, {
                                                           type: 'all',
                                                           text: 'All'
                                                       }]
                                                   },
                                                   navigator: {
                                                       enabled: true
                                                   },
                                                   scrollbar: {
                                                       enabled: true
                                                   },
                                                   title: {
                                                       text: symbol + ' Historical'
                                                   },
                                                   subtitle: {
                                                       text: 'With SMA and Volume by Price technical indicators'
                                                   },
                                                   xAxis:[{
                                                     type:'datetime'
                                                   }],
                                                   yAxis: [{
                                                       startOnTick: false,
                                                       endOnTick: false,
                                                       opposite:true,
                                                       labels: {
                                                           align: 'right',
                                                           x: -3
                                                       },
                                                       title: {
                                                           text: 'OHLC'
                                                       },
                                                       height: '60%',
                                                       lineWidth: 2,
                                                   }, {
                                                     opposite:true,
                                                       labels: {
                                                           align: 'right',
                                                           x: -3
                                                       },
                                                       title: {
                                                           text: 'Volume'
                                                       },
                                                       top: '65%',
                                                       height: '35%',
                                                       offset: 0,
                                                       lineWidth: 2
                                                   }],
                                                   tooltip: {
                                                       split: true
                                                   },
                                                   series: [{
                                                       type: 'candlestick',
                                                       name: symbol,
                                                       id: 'stock',
                                                       zIndex: 2,
                                                       data: ohlc // Assuming 'ohlc' is a predefined variable with your data
                                                   }, {
                                                       type: 'column',
                                                       name: 'Volume',
                                                       id: 'volume',
                                                       data: volume, // Assuming 'volume' is a predefined variable with your data
                                                       yAxis: 1,
                                                       color: '#4d4fd1',
                                                   }, {
                                                       type: 'vbp',
                                                       linkedTo: 'stock',
                                                       params: {
                                                           volumeSeriesID: 'volume'
                                                       },
                                                       dataLabels: {
                                                           enabled: false
                                                       },
                                                       zoneLines: {
                                                           enabled: false
                                                       }
                                                   }, {
                                                       type: 'sma',
                                                       linkedTo: 'stock',
                                                       zIndex: 1,
                                                       marker: {
                                                           enabled: false
                                                       }
                                                   }]
                                                 });

        }