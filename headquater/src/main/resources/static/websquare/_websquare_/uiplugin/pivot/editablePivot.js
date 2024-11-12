(function() {
    var callWithJQuery,
        indexOf = [].indexOf || function(item) {
            for (var i = 0, l = this.length; i < l; i++) {
                if (i in this && this[i] === item) return i;
            }
            return -1;
        },
        slice = [].slice,
        bind = function(fn, me) {
            return function() {
                return fn.apply(me, arguments);
            };
        },
        hasProp = {}.hasOwnProperty,
        Backbone,
        w5;
    callWithJQuery = function(pivotModule) {
        if (typeof exports === "object" && typeof module === "object") {
            return pivotModule(require("jquery"));
        } else if (typeof define === "function" && define.amd) {
            return define(["jquery"], pivotModule);
        } else {
            return pivotModule(jQuery, _, Backbone, w5);
        }
    };
    callWithJQuery(function($, _, Backbone, w5) {
        /*
        Utilities
         */
        var PivotData, addSeparators, aggregatorTemplates, aggregators, dayNamesEn, derivers, getSort, locales, mthNamesEn, naturalSort, numberFormat, pivotTableRenderer, renderers, sortAs, usFmt, usFmtInt, usFmtPct, zeroPad;
        addSeparators = function(nStr, thousandsSep, decimalSep) {
            var rgx, x, x1, x2;
            nStr += '';
            x = nStr.split('.');
            x1 = x[0];
            x2 = x.length > 1 ? decimalSep + x[1] : '';
            rgx = /(\d+)(\d{3})/;
            while (rgx.test(x1)) {
                x1 = x1.replace(rgx, '$1' + thousandsSep + '$2');
            }
            return x1 + x2;
        };
        numberFormat = function(opts) {
            var defaults;
            defaults = {
                digitsAfterDecimal: 2,
                scaler: 1,
                thousandsSep: ",",
                decimalSep: ".",
                prefix: "",
                suffix: "",
                showZero: false
            };
            opts = $.extend(defaults, opts);
            return function(x) {
                var result;
                var value;
                if (isNaN(x) || !isFinite(x)) {
                    return "";
                }
                if (x === 0 && !opts.showZero) {
                    return "";
                }
                if (opts.useNumberFormat == true && !isNaN(parseInt(opts.digitsAfterDecimal))) {
                    value = (parseFloat(opts.scaler) * x).toFixed(opts.digitsAfterDecimal + 1);
                    if (opts.digitsAfterDecimal > 0) {
                        value = value.substring(0, value.indexOf('.') + opts.digitsAfterDecimal + 1);
                    } else {
                        value = value.substring(0, value.indexOf('.'));
                    }
                } else {
                    value = x + "";
                }
                result = addSeparators(value, opts.thousandsSep, opts.decimalSep);
                if (isNaN(parseFloat(result))) {
                    return "" + result;
                } else {
                    return "" + opts.prefix + result + opts.suffix;
                }
            };
        };
        usFmt = numberFormat();
        usFmtInt = numberFormat({
            digitsAfterDecimal: 0
        });
        usFmtPct = numberFormat({
            digitsAfterDecimal: 1,
            scaler: 100,
            suffix: "%"
        });
        aggregatorTemplates = {
            uniqueValue: function(formatter) {
                if (formatter == null) {
                    formatter = usFmt;
                }
                return function(arg) {
                    var attr;
                    attr = arg[0];
                    return function() {
                        return {
                            uniqueVal: '',
                            push: function(record) {
                                if (record[attr] == "" || record[attr] == "null") {
                                    return this.uniqueVal = "";
                                } else {
                                    return this.uniqueVal = record[attr] + '';
                                }
                            },
                            value: function() {
                                return this.uniqueVal + '';
                            },
                            format: formatter
                        };
                    };
                };
            },
            count: function(formatter) {
                if (formatter == null) {
                    formatter = usFmtInt;
                }
                return function() {
                    return function() {
                        return {
                            count: 0,
                            push: function() {
                                return this.count++;
                            },
                            value: function() {
                                return this.count;
                            },
                            format: formatter
                        };
                    };
                };
            },
            countUnique: function(formatter) {
                if (formatter == null) {
                    formatter = usFmtInt;
                }
                return function(arg) {
                    var attr;
                    attr = arg[0];
                    return function() {
                        return {
                            uniq: [],
                            push: function(record) {
                                var ref = record[attr];
                                if (indexOf.call(this.uniq, ref) < 0) {
                                    return this.uniq.push(record[attr]);
                                }
                            },
                            value: function() {
                                return this.uniq.length;
                            },
                            format: formatter,
                            numInputs: attr != null ? 0 : 1
                        };
                    };
                };
            },
            listUnique: function(sep) {
                return function(arg) {
                    var attr;
                    attr = arg[0];
                    return function() {
                        return {
                            uniq: [],
                            push: function(record) {
                                var ref = record[attr];
                                if (indexOf.call(this.uniq, ref) < 0) {
                                    return this.uniq.push(record[attr]);
                                }
                            },
                            value: function() {
                                return this.uniq.join(sep);
                            },
                            format: function(x) {
                                return x;
                            },
                            numInputs: attr != null ? 0 : 1
                        };
                    };
                };
            },
            sum: function(formatter) {
                if (formatter == null) {
                    formatter = usFmt;
                }
                return function(arg) {
                    var attr;
                    attr = arg[0];
                    return function() {
                        return {
                            sum: 0,
                            push: function(record) {
                                if (!isNaN(parseFloat(record[attr]))) {
                                    this.sum += parseFloat(record[attr]);
                                    return this.sum;
                                }
                            },
                            value: function() {
                                return WebSquare.util.setPrecision(this.sum);
                            },
                            format: formatter,
                            numInputs: attr != null ? 0 : 1
                        };
                    };
                };
            },
            quantile: function(q, formatter) {
                if (formatter == null) {
                    formatter = usFmt;
                }
                return function(arg) {
                    var attr;
                    attr = arg[0];
                    return function(data, rowKey, colKey) {
                        return {
                            vals: [],
                            push: function(record) {
                                var x;
                                x = parseFloat(record[attr]);
                                if (!isNaN(x)) {
                                    return this.vals.push(x);
                                }
                            },
                            value: function() {
                                var i;
                                if (this.vals.length === 0) {
                                    return null;
                                }
                                this.vals.sort(function(a, b) {
                                    return a - b;
                                });
                                i = (this.vals.length - 1) * q;
                                return (this.vals[Math.floor(i)] + this.vals[Math.ceil(i)]) / 2.0;
                            },
                            format: formatter,
                            numInputs: attr != null ? 0 : 1
                        };
                    };
                };
            },
            runningStat: function(mode, ddof, formatter) {
                if (mode == null) {
                    mode = "mean";
                }
                if (ddof == null) {
                    ddof = 1;
                }
                if (formatter == null) {
                    formatter = usFmt;
                }
                return function(arg) {
                    var attr;
                    attr = arg[0];
                    return function(data, rowKey, colKey) {
                        return {
                            n: 0.0,
                            m: 0.0,
                            s: 0.0,
                            push: function(record) {
                                var m_new, x;
                                x = parseFloat(record[attr]);
                                if (isNaN(x)) {
                                    return;
                                }
                                this.n += 1.0;
                                if (this.n === 1.0) {
                                    return this.m = x;
                                } else {
                                    m_new = this.m + (x - this.m) / this.n;
                                    this.s = this.s + (x - this.m) * (x - m_new);
                                    return this.m = m_new;
                                }
                            },
                            value: function() {
                                if (mode === "mean") {
                                    if (this.n === 0) {
                                        return 0 / 0;
                                    } else {
                                        return this.m;
                                    }
                                }
                                if (this.n <= ddof) {
                                    return 0;
                                }
                                switch (mode) {
                                    case "var":
                                        return this.s / (this.n - ddof);
                                    case "stdev":
                                        return Math.sqrt(this.s / (this.n - ddof));
                                }
                            },
                            format: formatter,
                            numInputs: attr != null ? 0 : 1
                        };
                    };
                };
            },
            min: function(formatter) {
                if (formatter == null) {
                    formatter = usFmt;
                }
                return function(arg) {
                    var attr;
                    attr = arg[0];
                    return function() {
                        return {
                            val: null,
                            push: function(record) {
                                var ref, x;
                                x = parseFloat(record[attr]);
                                if (!isNaN(x)) {
                                    return this.val = Math.min(x, (ref = this.val) != null ? ref : x);
                                }
                            },
                            value: function() {
                                return this.val;
                            },
                            format: formatter,
                            numInputs: attr != null ? 0 : 1
                        };
                    };
                };
            },
            max: function(formatter) {
                if (formatter == null) {
                    formatter = usFmt;
                }
                return function(arg) {
                    var attr;
                    attr = arg[0];
                    return function() {
                        return {
                            val: null,
                            push: function(record) {
                                var ref, x;
                                x = parseFloat(record[attr]);
                                if (!isNaN(x)) {
                                    return this.val = Math.max(x, (ref = this.val) != null ? ref : x);
                                }
                            },
                            value: function() {
                                return this.val;
                            },
                            format: formatter,
                            numInputs: attr != null ? 0 : 1
                        };
                    };
                };
            },
            average: function(formatter) {
                if (formatter == null) {
                    formatter = usFmt;
                }
                return function(arg) {
                    var attr;
                    attr = arg[0];
                    return function() {
                        return {
                            sum: 0,
                            len: 0,
                            push: function(record) {
                                if (!isNaN(parseFloat(record[attr]))) {
                                    this.sum += parseFloat(record[attr]);
                                    return this.len++;
                                }
                            },
                            value: function() {
                                return WebSquare.util.setPrecision(this.sum / this.len);
                            },
                            format: formatter,
                            numInputs: attr != null ? 0 : 1
                        };
                    };
                };
            },
            sumOverSum: function(formatter) {
                if (formatter == null) {
                    formatter = usFmt;
                }
                return function(arg) {
                    var num = arg[0],
                        denom = arg[1];
                    return function() {
                        return {
                            sumNum: 0,
                            sumDenom: 0,
                            push: function(record) {
                                if (!isNaN(parseFloat(record[num]))) {
                                    this.sumNum += parseFloat(record[num]);
                                }
                                if (!isNaN(parseFloat(record[denom]))) {
                                    return this.sumDenom += parseFloat(record[denom]);
                                }
                            },
                            value: function() {
                                return this.sumNum / this.sumDenom;
                            },
                            format: formatter,
                            numInputs: (num != null) && (denom != null) ? 0 : 2
                        };
                    };
                };
            },
            sumOverSumBound80: function(upper, formatter) {
                if (upper == null) {
                    upper = true;
                }
                if (formatter == null) {
                    formatter = usFmt;
                }
                return function(arg) {
                    var num = arg[0],
                        denom = arg[1];
                    return function() {
                        return {
                            sumNum: 0,
                            sumDenom: 0,
                            push: function(record) {
                                if (!isNaN(parseFloat(record[num]))) {
                                    this.sumNum += parseFloat(record[num]);
                                }
                                if (!isNaN(parseFloat(record[denom]))) {
                                    return this.sumDenom += parseFloat(record[denom]);
                                }
                            },
                            value: function() {
                                var sign;
                                sign = upper ? 1 : -1;
                                return (0.821187207574908 / this.sumDenom + this.sumNum / this.sumDenom + 1.2815515655446004 * sign * Math.sqrt(0.410593603787454 / (this.sumDenom * this.sumDenom) + (this.sumNum * (1 - this.sumNum / this.sumDenom)) / (this.sumDenom * this.sumDenom))) / (1 + 1.642374415149816 / this.sumDenom);
                            },
                            format: formatter,
                            numInputs: (num != null) && (denom != null) ? 0 : 2
                        };
                    };
                };
            },
            fractionOf: function(wrapped, type, formatter) {
                if (type == null) {
                    type = "total";
                }
                if (formatter == null) {
                    formatter = usFmtPct;
                }
                return function() {
                    var x;
                    x = 1 <= arguments.length ? slice.call(arguments, 0) : [];
                    return function(data, rowKey, colKey) {
                        return {
                            selector: {
                                total: [
                                    [],
                                    []
                                ],
                                row: [rowKey, []],
                                col: [
                                    [], colKey
                                ]
                            } [type],
                            inner: wrapped.apply(null, x)(data, rowKey, colKey),
                            push: function(record) {
                                return this.inner.push(record);
                            },
                            format: formatter,
                            value: function() {
                                return this.inner.value() / data.getAggregator.apply(data, this.selector).inner.value();
                            },
                            numInputs: wrapped.apply(null, x)().numInputs
                        };
                    };
                };
            }
        };
        aggregatorTemplates.median = function(f) {
            return aggregatorTemplates.quantile(0.5, f);
        };
        aggregatorTemplates.stdev = function(ddof, f) {
            return aggregatorTemplates.runningStat("stdev", ddof, f);
        };
        aggregators = (function(tpl) {
            return {
                "UniqueValue": tpl.uniqueValue(usFmt),
                "Count": tpl.count(usFmtInt),
                "Count Unique Values": tpl.countUnique(usFmtInt),
                "List Unique Values": tpl.listUnique(", "),
                "Sum": tpl.sum(usFmt),
                "Median": tpl.median(usFmt),
                "Sample Standard Deviation": tpl.stdev(1, usFmt),
                "Integer Sum": tpl.sum(usFmtInt),
                "Average": tpl.average(usFmt),
                "Minimum": tpl.min(usFmt),
                "Maximum": tpl.max(usFmt),
                "Sum over Sum": tpl.sumOverSum(usFmt),
                "80% Upper Bound": tpl.sumOverSumBound80(true, usFmt),
                "80% Lower Bound": tpl.sumOverSumBound80(false, usFmt),
                "Sum as Fraction of Total": tpl.fractionOf(tpl.sum(), "total", usFmtPct),
                "Sum as Fraction of Rows": tpl.fractionOf(tpl.sum(), "row", usFmtPct),
                "Sum as Fraction of Columns": tpl.fractionOf(tpl.sum(), "col", usFmtPct),
                "Count as Fraction of Total": tpl.fractionOf(tpl.count(), "total", usFmtPct),
                "Count as Fraction of Rows": tpl.fractionOf(tpl.count(), "row", usFmtPct),
                "Count as Fraction of Columns": tpl.fractionOf(tpl.count(), "col", usFmtPct)
            };
        })(aggregatorTemplates);
        renderers = {
            "Table": function(pvtData, opts, w2parent) {
                return pivotTableRenderer(pvtData, opts, w2parent);
            },
            "Table Barchart": function(pvtData, opts) {
                return $(pivotTableRenderer(pvtData, opts)).barchart();
            },
            "Heatmap": function(pvtData, opts) {
                return $(pivotTableRenderer(pvtData, opts)).heatmap();
            },
            "Row Heatmap": function(pvtData, opts) {
                return $(pivotTableRenderer(pvtData, opts)).heatmap("rowheatmap");
            },
            "Col Heatmap": function(pvtData, opts) {
                return $(pivotTableRenderer(pvtData, opts)).heatmap("colheatmap");
            }
        };
        locales = {
            en: {
                aggregators: aggregators,
                renderers: renderers,
                localeStrings: {
                    renderError: "An error occurred rendering the PivotTable results.",
                    computeError: "An error occurred computing the PivotTable results.",
                    uiRenderError: "An error occurred rendering the PivotTable UI.",
                    selectAll: "Select All",
                    selectNone: "Select None",
                    tooMany: "(too many to list)",
                    filterResults: "Filter results",
                    totals: "Totals",
                    vs: "vs",
                    by: "by"
                }
            }
        };
        mthNamesEn = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        dayNamesEn = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
        zeroPad = function(number) {
            return ("0" + number).substr(-2, 2);
        };
        derivers = {
            bin: function(col, binWidth) {
                return function(record) {
                    return record[col] - record[col] % binWidth;
                };
            },
            dateFormat: function(col, formatString, utcOutput, mthNames, dayNames) {
                var utc;
                if (utcOutput == null) {
                    utcOutput = false;
                }
                if (mthNames == null) {
                    mthNames = mthNamesEn;
                }
                if (dayNames == null) {
                    dayNames = dayNamesEn;
                }
                utc = utcOutput ? "UTC" : "";
                return function(record) {
                    var date;
                    date = new Date(Date.parse(record[col]));
                    if (isNaN(date)) {
                        return "";
                    }
                    return formatString.replace(/%(.)/g, function(m, p) {
                        switch (p) {
                            case "y":
                                return date["get" + utc + "FullYear"]();
                            case "m":
                                return zeroPad(date["get" + utc + "Month"]() + 1);
                            case "n":
                                return mthNames[date["get" + utc + "Month"]()];
                            case "d":
                                return zeroPad(date["get" + utc + "Date"]());
                            case "w":
                                return dayNames[date["get" + utc + "Day"]()];
                            case "x":
                                return date["get" + utc + "Day"]();
                            case "H":
                                return zeroPad(date["get" + utc + "Hours"]());
                            case "M":
                                return zeroPad(date["get" + utc + "Minutes"]());
                            case "S":
                                return zeroPad(date["get" + utc + "Seconds"]());
                            default:
                                return "%" + p;
                        }
                    });
                };
            }
        };
        naturalSort = (function() {
            return function(as, bs) {
                var a, a1, b, b1, rd, rx, rz;
                rx = /(\d+)|(\D+)/g;
                rd = /\d/;
                rz = /^0/;
                if (typeof as === "number" || typeof bs === "number") {
                    if (isNaN(as)) {
                        return 1;
                    }
                    if (isNaN(bs)) {
                        return -1;
                    }
                    return as - bs;
                }
                a = String(as).toLowerCase();
                b = String(bs).toLowerCase();
                if (a === b) {
                    return 0;
                }
                if (!(rd.test(a) && rd.test(b))) {
                    return (a > b ? 1 : -1);
                }
                a = a.match(rx);
                b = b.match(rx);
                while (a.length && b.length) {
                    a1 = a.shift();
                    b1 = b.shift();
                    if (a1 !== b1) {
                        if (rd.test(a1) && rd.test(b1)) {
                            return a1.replace(rz, ".0") - b1.replace(rz, ".0");
                        } else {
                            return (a1 > b1 ? 1 : -1);
                        }
                    }
                }
                return a.length - b.length;
            };
        })(this);
        sortAs = function(order) {
            var i, mapping, x;
            mapping = {};
            for (i in order) {
                x = order[i];
                mapping[x] = i;
            }
            return function(a, b) {
                if ((mapping[a] != null) && (mapping[b] != null)) {
                    return mapping[a] - mapping[b];
                } else if (mapping[a] != null) {
                    return -1;
                } else if (mapping[b] != null) {
                    return 1;
                } else {
                    return naturalSort(a, b);
                }
            };
        };
        getSort = function(sorters, attr) {
            var sort;
            sort = sorters(attr);
            if ($.isFunction(sort)) {
                return sort;
            } else {
                return naturalSort;
            }
        };
        $.pivotUtilities = {
            aggregatorTemplates: aggregatorTemplates,
            aggregators: aggregators,
            renderers: renderers,
            derivers: derivers,
            locales: locales,
            naturalSort: naturalSort,
            numberFormat: numberFormat,
            sortAs: sortAs
        };
        /*
        Data Model class
         */
        PivotData = (function() {
            function PivotData(input, opts, w2parent) {
                this.w2parent = w2parent;
                this.processedCount = -1;
                this.getAggregator = bind(this.getAggregator, this);
                this.getRowKeys = bind(this.getRowKeys, this);
                this.getColKeys = bind(this.getColKeys, this);
                this.sortKeys = bind(this.sortKeys, this);
                this.arrSort = bind(this.arrSort, this);
                this.aggregator = opts.aggregator;
                this.aggregators = opts.aggregators;
                this.aggregatorName = opts.aggregatorName;
                this.colAttrs = opts.cols;
                this.rowAttrs = opts.rows;
                this.valAttrs = opts.vals;
                this.sorters = opts.sorters;
                this.tree = {};
                this.rowKeys = [];
                this.colKeys = [];
                this.longestTextWidthRowKey = [];
                this.longestTextWidthColKey = [];
                this.realRowIndexByColKeys = {};
                this.realRowIndexByRowKeys = {};
                this.rowKeyStrings = {};
                this.colKeyStrings = {};
                this.rowTotals = {};
                this.colTotals = {};
                this.allTotal = opts.allTotals;
                this.sorted = false;
                this.valuesAxis = opts.valuesAxis || 'row';
                this.mappingDataInfoByKey = {};
                this.mappingDataInfoByRowIndex = {};
                if (this.valuesAxis !== 'row' && this.valAttrs.length === 1 && this.rowAttrs.length === 0 && this.colAttrs.length === 0) {
                    this.valuesAxis = 'row';
                }
                if (this.valAttrs) {
                    if (w2parent.options.showGrandTotal == true) {
                        _.each(this.allTotal, function(totalAggregator, val, obj) {
                            obj[val] = totalAggregator(this, [], []);
                        }, this);
                    }
                    PivotData.forEachRecord(input, opts.derivedAttributes, (function(_this, w2parent) {
                        return function(record, rowIndex) {
                            var rst = [];
                            if (opts.filter(record)) {
                                var rFlag = false;
                                for (var i = 0; i < _this.valAttrs.length; i++) {
                                    if (i == 0) {
                                        rFlag = true;
                                    } else {
                                        rFlag = false;
                                    }
                                    rst.push(_this.processRecord(record, _this.valAttrs[i], rFlag, opts, w2parent, rowIndex));
                                }
                                return rst;
                            }
                        };
                    })(this));
                } else {
                    for (var l = 0, len = input.length; l < len; l++) {
                        this.processHeader(input[l]);
                    }
                }
            }
            PivotData.forEachRecord = function(input, derivedAttributes, f) {
                var addRecord, compactRecord, i, j, k, l, len1, record, ref, results, results1, tblCols;
                if ($.isEmptyObject(derivedAttributes)) {
                    addRecord = f;
                } else {
                    addRecord = function(record, rIndex) {
                        var k, ref, v;
                        for (k in derivedAttributes) {
                            v = derivedAttributes[k];
                            record[k] = (ref = v(record)) != null ? ref : record[k];
                        }
                        return f(record, rIndex);
                    };
                }
                if ($.isFunction(input)) {
                    return input(addRecord);
                } else if ($.isArray(input)) {
                    if ($.isArray(input[0])) {
                        results = [];
                        for (i in input) {
                            if (!hasProp.call(input, i)) continue;
                            compactRecord = input[i];
                            if (!(i > 0)) {
                                continue;
                            }
                            record = {};
                            ref = input[0];
                            for (j in ref) {
                                if (!hasProp.call(ref, j)) continue;
                                k = ref[j];
                                record[k] = compactRecord[j];
                            }
                            results.push(addRecord(record));
                        }
                        return results;
                    } else {
                        results1 = [];
                        for (l = 0, len1 = input.length; l < len1; l++) {
                            record = input[l];
                            Array.prototype.push.apply(results1, addRecord(record, l));
                        }
                        return results1;
                    }
                } else if (input instanceof jQuery) {
                    tblCols = [];
                    $("thead > tr > th", input).each(function() {
                        return tblCols.push($(this).text());
                    });
                    return $("tbody > tr", input).each(function() {
                        record = {};
                        $("td", this).each(function(j) {
                            return record[tblCols[j]] = $(this).html();
                        });
                        return addRecord(record);
                    });
                } else {
                    throw new Error("unknown input format");
                }
            };
            PivotData.convertToArray = function(input) {
                var result;
                result = [];
                PivotData.forEachRecord(input, {}, function(record) {
                    return result.push(record);
                });
                return result;
            };
            PivotData.prototype.arrSort = function(attrs) {
                var a, sortersArr;
                sortersArr = (function() {
                    var l, len1, results;
                    results = [];
                    for (l = 0, len1 = attrs.length; l < len1; l++) {
                        a = attrs[l];
                        results.push(getSort(this.sorters, a));
                    }
                    return results;
                }).call(this);
                return function(a, b) {
                    var comparison, i, sorter;
                    for (i in sortersArr) {
                        sorter = sortersArr[i];
                        comparison = sorter(a[i], b[i]);
                        if (comparison !== 0) {
                            return comparison;
                        }
                    }
                    return 0;
                };
            };
            PivotData.prototype.sortKeys = function() {
                if (!this.sorted) {
                    this.sorted = true;
                    this.rowKeys.sort(this.arrSort(this.rowAttrs));
                    return this.colKeys.sort(this.arrSort(this.colAttrs));
                }
            };
            PivotData.prototype.getColKeys = function() {
                this.sortKeys();
                return this.colKeys;
            };
            PivotData.prototype.getLongestTestWidthColKey = function() {
                return this.longestTextWidthColKey;
            };
            PivotData.prototype.getRealRowIndexByColKeys = function() {
                return this.realRowIndexByColKeys;
            };
            PivotData.prototype.getRowKeys = function() {
                this.sortKeys();
                return this.rowKeys;
            };
            PivotData.prototype.getStringWidth = function(str) {
                var width = 0;
                if (str.indexOf("<img") > -1) {
                    width += this.w2parent.options.headerDataImageWidth;
                    str = str.replace(/[<][^>]*[>]/g, "");
                }
                str = str.replace(/&nbsp;/g, " ");
                for (var i = 0; i < str.length; i++) {
                    var codeValue = str[i].charCodeAt();
                    if (codeValue >= 33 && codeValue <= 133) {
                        width += this.w2parent.charWidth[codeValue.toString()];
                    } else if (codeValue == 32) {
                        width += this.w2parent.charWidth["emptyString"];
                    } else { //한글
                        width += 12;
                    }
                }
                return width;
            };
            PivotData.prototype.getLongestTestWidthRowKey = function() {
                return this.longestTextWidthRowKey;
            };
            PivotData.prototype.getRealRowIndexByRowKeys = function() {
                return this.realRowIndexByRowKeys;
            };
            PivotData.prototype.updateRecord = function(info) {
                var val = info.val;
                var colKey = info.colKey;
                var rowKey = info.rowKey;
                var record = info.record;
                var rowIndex = info.rowIndex;
                var flatRowKey = rowKey.join(String.fromCharCode(0));
                var flatColKey = colKey.join(String.fromCharCode(0));
                if (this.w2parent.options.showGrandTotal == true) {
                    this.allTotal[val].sum -= info.oldValue;
                    this.allTotal[val].push(record);
                    if (rowKey.length !== 0) {
                        this.rowTotals[flatRowKey][val].sum -= info.oldValue;
                        this.rowTotals[flatRowKey][val].push(record);
                    }
                    if (colKey.length !== 0) {
                        this.colTotals[flatColKey][val].sum -= info.oldValue;
                        this.colTotals[flatColKey][val].push(record);
                    }
                }
                if (!this.mappingDataInfoByKey[flatRowKey]) {
                    this.mappingDataInfoByKey[flatRowKey] = {};
                }
                if (!this.mappingDataInfoByKey[flatRowKey][flatColKey]) {
                    this.mappingDataInfoByKey[flatRowKey][flatColKey] = {};
                }
                this.mappingDataInfoByKey[flatRowKey][flatColKey]["mappedRowIndex"] = rowIndex;
                this.mappingDataInfoByKey[flatRowKey][flatColKey]["mappedColID"] = val;
                this.mappingDataInfoByKey[flatRowKey][flatColKey]["record"] = record;
                this.mappingDataInfoByRowIndex[rowIndex][val] = {
                    record: record,
                    rowKey: rowKey,
                    colKey: colKey,
                    flatRowKey: flatRowKey,
                    flatColKey: flatColKey
                }
                if (colKey.length !== 0 && rowKey.length !== 0) {
                    this.tree[flatRowKey][flatColKey] = this.aggregators[val](this, rowKey, colKey);
                    this.tree[flatRowKey][flatColKey].push(record);
                }
            };
            PivotData.prototype.processRecord = function(record, val, rFlag, opts, w2parent, realRowIndex) {
                // STEP 0
                var colKey, flatColKey, flatRowKey, l, len1, len2, n, ref, ref1, ref2, ref3, rowKey, rowIndex;
                colKey = [];
                rowKey = [];
                if (rFlag) {
                    this.processedCount++;
                }
                rowIndex = this.processedCount;
                ref = this.colAttrs;
                for (l = 0, len1 = ref.length; l < len1; l++) {
                    if (ref[l] === this.w2parent.options.grandTotalName) {
                        colKey.push(val);
                    } else {
                        ref1 = record[ref[l]];
                        if (ref1 != null) {
                            colKey.push(ref1);
                        }
                    }
                }
                ref2 = this.rowAttrs;
                for (n = 0, len2 = ref2.length; n < len2; n++) {
                    if (ref2[n] === this.w2parent.options.grandTotalName) {
                        rowKey.push(val);
                    } else {
                        ref3 = record[ref2[n]];
                        if (ref3 != null) {
                            rowKey.push(ref3);
                        }
                    }
                }
                flatRowKey = rowKey.join(String.fromCharCode(0));
                flatColKey = colKey.join(String.fromCharCode(0));
                if (this.w2parent.options.showGrandTotal == true) {
                    this.allTotal[val].push(record);
                }
                if (rowKey.length !== 0) {
                    if (!this.rowTotals[flatRowKey]) {
                        this.rowTotals[flatRowKey] = {};
                        this.rowKeys.push(rowKey);
                        var rowKeyStr;
                        var formattedStr;
                        for (var i = 0, rowKeyLength = rowKey.length; i < rowKeyLength; i++) {
                            if (!this.rowKeyStrings[i]) {
                                this.rowKeyStrings[i] = {};
                            }
                            rowKeyStr = rowKey[i] + '';
                            if (this.longestTextWidthRowKey[i] || this.longestTextWidthRowKey[i] == "") {
                                if (this.w2parent.headerDataNameFormatter) {
                                    if (this.rowKeyStrings[i][rowKeyStr]) {
                                        formattedStr = this.rowKeyStrings[i][rowKeyStr];
                                    } else {
                                        formattedStr = this.w2parent.headerDataNameFormatter(realRowIndex, this.rowAttrs[i], rowKeyStr, this.w2parent.data[realRowIndex]);
                                        this.rowKeyStrings[i][rowKeyStr] = formattedStr;
                                    }
                                    if (this.longestTextWidthRowKey[i] != formattedStr && this.getStringWidth(this.longestTextWidthRowKey[i]) < this.getStringWidth(formattedStr)) {
                                        this.longestTextWidthRowKey[i] = formattedStr;
                                    }
                                } else {
                                    if (this.longestTextWidthRowKey[i] != rowKeyStr && this.getStringWidth(this.longestTextWidthRowKey[i]) < this.getStringWidth(rowKeyStr)) {
                                        this.longestTextWidthRowKey[i] = rowKeyStr;
                                    }
                                }
                            } else {
                                if (this.w2parent.headerDataNameFormatter) {
                                    if (this.rowKeyStrings[i][rowKeyStr]) {
                                        formattedStr = this.rowKeyStrings[i][rowKeyStr];
                                    } else {
                                        formattedStr = this.w2parent.headerDataNameFormatter(realRowIndex, this.rowAttrs[i], rowKeyStr, this.w2parent.data[realRowIndex]);
                                        this.rowKeyStrings[i][rowKeyStr] = formattedStr;
                                    }
                                    this.longestTextWidthRowKey.push(formattedStr);
                                } else {
                                    this.longestTextWidthRowKey.push(rowKeyStr);
                                }
                            }
                        }
                        this.realRowIndexByRowKeys[flatRowKey] = realRowIndex;
                    }
                    if (this.w2parent.options.showGrandTotal == true) {
                        if (!this.rowTotals[flatRowKey][val]) {
                            this.rowTotals[flatRowKey][val] = this.aggregators[val](this, rowKey, []);
                        }
                        this.rowTotals[flatRowKey][val].push(record);
                    }
                }
                if (colKey.length !== 0) {
                    if (!this.colTotals[flatColKey]) {
                        this.colTotals[flatColKey] = {};
                        this.colKeys.push(colKey);
                        var colKeyStr;
                        var formattedStr;
                        for (var i = 0, colKeyLength = colKey.length; i < colKeyLength; i++) {
                            if (!this.colKeyStrings[i]) {
                                this.colKeyStrings[i] = {};
                            }
                            colKeyStr = colKey[i] + '';
                            if (this.longestTextWidthColKey[i]) {
                                if (this.w2parent.headerDataNameFormatter) {
                                    if (this.colKeyStrings[i][colKeyStr]) {
                                        formattedStr = this.colKeyStrings[i][colKeyStr];
                                    } else {
                                        formattedStr = this.w2parent.headerDataNameFormatter(realRowIndex, this.colAttrs[i], colKeyStr, this.w2parent.data[realRowIndex]);
                                        this.colKeyStrings[i][colKeyStr] = formattedStr;
                                    }
                                    if (this.longestTextWidthColKey[i] != colKeyStr && this.getStringWidth(this.longestTextWidthColKey[i]) < this.getStringWidth(formattedStr)) {
                                        this.longestTextWidthColKey[i] = formattedStr;
                                    }
                                } else {
                                    if (this.longestTextWidthColKey[i] != colKeyStr && this.getStringWidth(this.longestTextWidthColKey[i]) < this.getStringWidth(colKeyStr)) {
                                        this.longestTextWidthColKey[i] = colKeyStr;
                                    }
                                }
                            } else {
                                if (this.w2parent.headerDataNameFormatter) {
                                    if (this.colKeyStrings[i][colKeyStr]) {
                                        formattedStr = this.colKeyStrings[i][colKeyStr];
                                    } else {
                                        formattedStr = this.w2parent.headerDataNameFormatter(realRowIndex, this.colAttrs[i], colKeyStr, this.w2parent.data[realRowIndex]);
                                        this.colKeyStrings[i][colKeyStr] = formattedStr;
                                    }
                                    this.longestTextWidthColKey.push(formattedStr);
                                } else {
                                    this.longestTextWidthColKey.push(colKey[i] + '');
                                }
                            }
                        }
                        this.realRowIndexByColKeys[flatColKey] = realRowIndex;
                    }
                    if (this.w2parent.options.showGrandTotal == true) {
                        if (!this.colTotals[flatColKey][val]) {
                            this.colTotals[flatColKey][val] = this.aggregators[val](this, [], colKey);
                        }
                        this.colTotals[flatColKey][val].push(record);
                    }
                }
                if (!this.mappingDataInfoByKey[flatRowKey]) {
                    this.mappingDataInfoByKey[flatRowKey] = {};
                }
                if (!this.mappingDataInfoByKey[flatRowKey][flatColKey]) {
                    this.mappingDataInfoByKey[flatRowKey][flatColKey] = {};
                }
                this.mappingDataInfoByKey[flatRowKey][flatColKey]["mappedRowIndex"] = realRowIndex;
                this.mappingDataInfoByKey[flatRowKey][flatColKey]["mappedColID"] = val;
                this.mappingDataInfoByKey[flatRowKey][flatColKey]["record"] = record;
                if (!this.mappingDataInfoByRowIndex[realRowIndex]) {
                    this.mappingDataInfoByRowIndex[realRowIndex] = {};
                }
                this.mappingDataInfoByRowIndex[realRowIndex][val] = {
                    record: record,
                    rowKey: rowKey,
                    colKey: colKey,
                    flatRowKey: flatRowKey,
                    flatColKey: flatColKey
                }
                if (colKey.length !== 0 && rowKey.length !== 0) {
                    if (!this.tree[flatRowKey]) {
                        this.tree[flatRowKey] = {};
                    }
                    if (!this.tree[flatRowKey][flatColKey]) {
                        this.tree[flatRowKey][flatColKey] = this.aggregators[val](this, rowKey, colKey);
                    }
                    return this.tree[flatRowKey][flatColKey].push(record);
                }
            };
            PivotData.prototype.processHeader = function(record) {
                var ref, len, i,
                    colKey = [],
                    rowKey = [],
                    flatRowKey, flatColKey;
                ref = this.colAttrs;
                for (i = 0, len = ref.length; i < len; i++) {
                    colKey.push(record[ref[i]] || null);
                }
                ref = this.rowAttrs;
                for (i = 0, len = ref.length; i < len; i++) {
                    rowKey.push(record[ref[i]] || null);
                }
                flatRowKey = rowKey.join(String.fromCharCode(0));
                flatColKey = colKey.join(String.fromCharCode(0));
                if (rowKey.length !== 0) {
                    if (!this.rowTotals[flatRowKey]) {
                        this.rowKeys.push(rowKey);
                    }
                }
                if (colKey.length !== 0) {
                    if (!this.colTotals[flatColKey]) {
                        this.colKeys.push(colKey);
                    }
                }
            };
            PivotData.prototype.getAggregator = function(rowKey, colKey, val) {
                var agg, flatColKey, flatRowKey;
                if (rowKey instanceof Array || colKey instanceof Array) {
                    flatRowKey = rowKey.join(String.fromCharCode(0));
                    flatColKey = colKey.join(String.fromCharCode(0));
                } else {
                    flatRowKey = rowKey;
                    flatColKey = colKey;
                }
                if (rowKey.length === 0 && colKey.length === 0) {
                    if (this.w2parent.options.showGrandTotal == true) {
                        agg = this.allTotal[val];
                    }
                } else if (rowKey.length === 0) {
                    if (this.w2parent.options.showGrandTotal == true) {
                        agg = this.colTotals[flatColKey][val];
                    }
                } else if (colKey.length === 0) {
                    if (this.w2parent.options.showGrandTotal == true) {
                        agg = this.rowTotals[flatRowKey][val];
                    }
                } else {
                    agg = this.tree[flatRowKey][flatColKey];
                }
                return agg != null ? agg : {
                    value: (function() {
                        return null;
                    }),
                    format: function() {
                        return "";
                    }
                };
            };
            PivotData.prototype.resizeContentArea = function(result, w2parent) {
                try {
                    var table = $(result);
                    var render = $(w2parent.render);
                    var oneCell;
                    if (w2parent.pivotDOM.tdMap[0]) {
                        oneCell = w2parent.pivotDOM.tdMap[0][0] ? $(w2parent.pivotDOM.tdMap[0][0]) : undefined;
                    }
                    var containerHeader = $("#" + w2parent.id + "_container_header");
                    var tableHeaderTrList = table[0].getElementsByClassName('w2editablePivot_contentTable_header_TR');
                    var tableContentTrThList = [];
                    if (table[0].getElementsByClassName('w2editablePivot_contentTable_content_TR')[0]) {
                        tableContentTrThList = table[0].getElementsByClassName('w2editablePivot_contentTable_content_TR')[0].getElementsByClassName('type7');
                    }
                    var cellSize;
                    if (oneCell) {
                        if (w2parent.cellWidth) {
                            cellSize = {
                                width: w2parent.cellWidth,
                                height: w2parent.cellHeight
                            }
                        } else {
                            var maxWidth = oneCell[0].getBoundingClientRect().width;
                            for (var j = 0; j < w2parent.pivotDOM.tdMap[0].length; j++) {
                                if (w2parent.pivotDOM.tdMap[0][j]) {
                                    var colWidth = w2parent.pivotDOM.tdMap[0][j].getBoundingClientRect().width;
                                    if (maxWidth < colWidth) {
                                        maxWidth = colWidth;
                                    }
                                }
                            }
                            if (w2parent.options.cellMinWidth) {
                                maxWidth = w2parent.options.cellMinWidth;
                            } else {
                                if (maxWidth < w2parent.options.defalutCellWidth) {
                                    maxWidth = w2parent.options.defalutCellWidth;
                                }
                            }
                            cellSize = {
                                width: maxWidth,
                                height: oneCell[0].getBoundingClientRect().height
                            }
                        }
                    } else {
                        var tempWidth = w2parent.options.defalutCellWidth;;
                        if (w2parent.options.cellMinWidth) {
                            tempWidth = w2parent.options.cellMinWidth;
                        }
                        cellSize = {
                            width: tempWidth + 2,
                            height: w2parent.options.cellMinHeight + 2
                        }
                    }
                    var viewSize = {
                        width: Math.floor(render[0].getBoundingClientRect().width),
                        //height:  Math.floor( ( render[0].getBoundingClientRect().height - containerHeader[0].getBoundingClientRect().height ) )
                        height: Math.floor((render[0].getBoundingClientRect().height - containerHeader.outerHeight(true)))
                    }
                    var axisAreaSize = {
                        width: 0,
                        height: 0
                    }
                    var colGroupWidth = [];
                    for (var i = 0; i < tableHeaderTrList.length; i++) {
                        axisAreaSize.height += $(tableHeaderTrList[i])[0].getBoundingClientRect().height;
                    }
                    var colAttrWidth = 0;
                    for (var j = 0; j < tableContentTrThList.length; j++) {
                        var thWidth = 0;
                        var element = $(tableContentTrThList[j])[0];
                        colAttrWidth = 0;
                        if ((j == (tableContentTrThList.length - 1)) &&
                            (this.rowAttrs.length != 0 && this.rowAttrs[0] != w2parent.options.grandTotalName) &&
                            (this.colAttrs.length != 0 && this.colAttrs[0] != w2parent.options.grandTotalName)) {
                            colAttrWidth = table[0].getElementsByClassName('w2editablePivot_contentTable_header_TR')[0].childNodes[1].getBoundingClientRect().width;
                            thWidth = element.getBoundingClientRect().width;
                        } else {
                            thWidth = element.getBoundingClientRect().width;
                        }
                        if (element.innerHTML.indexOf("<img") > -1) {
                            var firstColumnColGroupWidth = $("#" + w2parent.id + "_col1").width();
                            if (!firstColumnColGroupWidth) {
                                thWidth += this.w2parent.options.headerDataImageWidth;
                            }
                        }
                        axisAreaSize.width += thWidth;
                        colGroupWidth.push(thWidth - colAttrWidth);
                        $(tableContentTrThList[j])[0].innerHTML = w2parent.firstRowStr[j];
                    }
                    if (colAttrWidth != 0) {
                        colGroupWidth.push(colAttrWidth);
                    }
                    var grandTotalSize = {
                        width: 0,
                        height: 0
                    }
                    if (w2parent.options.showGrandTotal) {
                        if (this.valuesAxis == 'col') {
                            if (w2parent.options.grandTotalDisplayType == "both" || w2parent.options.grandTotalDisplayType == "vertical") {
                                grandTotalSize.width += this.valAttrs.length * cellSize.width;
                            }
                            grandTotalSize.height += cellSize.height;
                        } else {
                            if (w2parent.options.grandTotalDisplayType == "both" || w2parent.options.grandTotalDisplayType == "vertical") {
                                grandTotalSize.width += cellSize.width;
                            }
                            grandTotalSize.height += this.valAttrs.length * cellSize.height;
                        }
                    }
                    // 각 column width 값 저장
                    w2parent.totalDataCellWidth = 0;
                    var addedCell = 0;
                    w2parent.maxViewCellWidthTotal = 0;
                    w2parent.colWidthInfo = {};
                    if (w2parent.options.showGrandTotal) {
                        if (w2parent.options.grandTotalDisplayType == "horizontal") {
                            addedCell = 0;
                        } else {
                            addedCell = 1;
                            if (this.valuesAxis == "col") {
                                addedCell = this.valAttrs.length;
                            }
                        }
                    }
                    for (var j = 0; j < this.colKeys.length + addedCell; j++) {
                        var colWidth = 0;
                        if (w2parent.pivotDOM.tdMap[0] && w2parent.pivotDOM.tdMap[0][j] && w2parent.colWidthInfo[j]) {
                            colWidth = w2parent.pivotDOM.tdMap[0][j].getBoundingClientRect().width;
                        }
                        w2parent.totalDataCellWidth += colWidth ? colWidth : cellSize.width;
                        w2parent.colWidthInfo[j] = colWidth ? colWidth : cellSize.width;
                        if (j < w2parent.maxViewCellX) {
                            w2parent.maxViewCellWidthTotal += w2parent.colWidthInfo[j];
                        }
                    }
                    w2parent.colWidthInfo.length = j;
                    var tableSize = {
                        width: Math.ceil(w2parent.totalDataCellWidth + axisAreaSize.width + grandTotalSize.width),
                        height: Math.ceil((this.rowKeys.length * cellSize.height) + axisAreaSize.height + grandTotalSize.height)
                    }
                    // 1. X가 넘는가?
                    //    넘으면 : x show 후, bodyHeight 축소, bar 작업
                    //    넘지않으면 : x hidden 후, bodyHeight 증가.
                    // 2. Y가 넘는가?
                    //    넘으면 : Y show 후, contentWidth 축소, bar 작업
                    //    넘지않으면 : Y를 hidden 후, contentWidth 100%
                    var scrollY = $("#" + w2parent.id + "_container_scrollY");
                    var scrollX = $("#" + w2parent.id + "_container_scrollX");
                    var containerBody = $("#" + w2parent.id + "_container_body");
                    var containerContent = $("#" + w2parent.id + "_container_content");
                    //scroll 초기화
                    if (scrollX[0]) {
                        scrollX[0].scrollLeft = 0;
                    }
                    if (scrollY[0]) {
                        scrollY[0].scrollTop = 0;
                    }
                    if (w2parent.options.autoFit != "allColumn") {
                        if (viewSize.width < tableSize.width) {
                            scrollX.show();
                            // var bodyHeight = render[0].getBoundingClientRect().height -containerHeader[0].getBoundingClientRect().height -scrollX[0].getBoundingClientRect().height;
                            var bodyHeight = render.height() - containerHeader.outerHeight(true) - scrollX[0].offsetHeight;
                            containerBody.height(bodyHeight);
                            containerContent.height(bodyHeight);
                            $("#" + w2parent.id + "_container_scrollX_barX").css("width", tableSize.width);
                            viewSize.height -= scrollX[0].offsetHeight;
                        } else {
                            scrollX.hide();
                            var bodyHeight = render.height() - containerHeader.outerHeight(true);
                            // render[0].getBoundingClientRect().height -containerHeader[0].getBoundingClientRect().height;
                            containerBody.height(bodyHeight);
                            if (w2parent.options.editablePivotMode == "true") {
                                bodyHeight -= scrollX[0].offsetHeight;
                            }
                            containerContent.height(bodyHeight);
                        }
                    }
                    var contentWidth = 0;
                    if (viewSize.height < tableSize.height) {
                        scrollY.show();
                        contentWidth = render.width() - scrollY.width() - 1; //scrollY 사라지는 문제로 1px 줄임
                        containerContent.width(contentWidth);
                        $("#" + w2parent.id + "_container_scrollY_barY").css("height", tableSize.height);
                    } else {
                        scrollY.hide();
                        contentWidth = render.width(); //render[0].getBoundingClientRect().width;
                        containerContent.width(contentWidth);
                    }
                    containerHeader.width(contentWidth);
                    if (w2parent.options.renderMode == "full") {
                        var headerList = w2parent.render.getElementsByClassName('w2editablePivot_container_header')[0].getElementsByTagName('div');
                        for (var i = 0; i < headerList.length; i++) {
                            var hDiv = headerList[i];
                            $(hDiv).width(contentWidth - 12);
                        }
                    }
                    //w2parent.maxViewCellX = ( Math.floor((viewSize.width-axisAreaSize.width)/cellSize.width) ) +1;
                    //w2parent.maxViewCellY = ( Math.floor((containerBody.height()-axisAreaSize.height)/cellSize.height) )+3;
                    w2parent.pivotDOM.pivotTABLE = result;
                    w2parent.pivotWidth = 0;
                    var axisAreaTh = tableHeaderTrList[tableHeaderTrList.length - 1].childNodes;
                    if (w2parent.options.rows.length === 0 && axisAreaTh.length > 0) {
                        axisAreaTh = [];
                        axisAreaTh.push(tableHeaderTrList[tableHeaderTrList.length - 1].childNodes[0]);
                    }
                    var axisAreaThWidth = 0;
                    for (var i = 0; i < axisAreaTh.length; i++) {
                        var width = 0;
                        if (colGroupWidth[i]) {
                            width = colGroupWidth[i];
                        } else {
                            width = axisAreaTh[i].getBoundingClientRect().width;
                        }
                        $("#" + w2parent.id + "_col" + i).width(width);
                        axisAreaThWidth += width;
                    }
                    w2parent.pivotWidth = axisAreaThWidth;
                    w2parent.pivotHeaderXWidth = axisAreaThWidth;
                    var cellWidth;
                    if (w2parent.pivotDOM.colGroup) {
                        var valAttrsColumnCount = 0;
                        if (w2parent.options.showGrandTotal) {
                            if (w2parent.options.grandTotalDisplayType == "horizontal") {
                                valAttrsColumnCount = 0;
                            } else {
                                if (this.valuesAxis == 'col') {
                                    valAttrsColumnCount = this.valAttrs.length;
                                } else {
                                    valAttrsColumnCount = 1;
                                }
                            }
                        }
                        var autofitColumnCount = w2parent.pivotDOM.colGroup.length - axisAreaTh.length - 10 + valAttrsColumnCount;
                        var totalAutofitColumnWidth = viewSize.width - axisAreaThWidth - 1 - (scrollY.is(':visible') ? scrollY.width() + 1 : 0);
                        var autofitColumnWidth = 0;
                        var autofitColumnRemainder = 0;
                        if (w2parent.options.autoFit == "allColumn") {
                            var minWidth = parseInt(w2parent.options.autoFitMinWidth);
                            if (!isNaN(minWidth)) {
                                totalAutofitColumnWidth = Math.max(minWidth - axisAreaThWidth - 1, totalAutofitColumnWidth);
                            }
                            autofitColumnWidth = Math.floor(totalAutofitColumnWidth / autofitColumnCount);
                            if (w2parent.options.autoFitCellMinWidth > autofitColumnWidth) {
                                cellWidth = w2parent.options.autoFitCellMinWidth;
                            } else {
                                cellWidth = autofitColumnWidth;
                            }
                            if (w2parent.options.showGrandTotal) {
                                if (w2parent.options.grandTotalDisplayType != "horizontal") {
                                    if (this.valuesAxis == 'col') {
                                        grandTotalSize.width = valAttrsColumnCount * autofitColumnWidth;
                                    } else {
                                        grandTotalSize.width = autofitColumnWidth;
                                    }
                                }
                            }
                        } else {
                            cellWidth = cellSize.width;
                        }
                        autofitColumnRemainder = totalAutofitColumnWidth - (autofitColumnWidth * autofitColumnCount);
                    }
                    w2parent.defaultColCnt = axisAreaTh.length;
                    if (w2parent.options.autoFit) {
                        w2parent.maxViewCellWidthTotal = 0;
                    }
                    if (w2parent.pivotDOM.colGroup) {
                        for (var i2 = axisAreaTh.length; i2 < w2parent.pivotDOM.colGroup.length; i2++, autofitColumnRemainder--) {
                            var width = cellWidth;
                            if (w2parent.options.autoFit && i2 < (w2parent.pivotDOM.colGroup.length - 10)) {
                                if (w2parent.options.autoFit == "lastColumn") {
                                    if (i2 == (w2parent.pivotDOM.colGroup.length - 1 - 10)) {
                                        width = Math.max(viewSize.width - w2parent.maxViewCellWidthTotal - axisAreaThWidth - 1, cellSize.width) - scrollY.width() - 3;
                                        w2parent.maxViewCellWidthTotal += width;
                                        w2parent.maxViewCellWidthTotal -= w2parent.colWidthInfo[i2 - axisAreaTh.length];
                                        w2parent.colWidthInfo[i2 - axisAreaTh.length] = width;
                                    } else {
                                        width = cellWidth;
                                        w2parent.maxViewCellWidthTotal += width;
                                        w2parent.colWidthInfo[i2 - axisAreaTh.length] = width;
                                    }
                                } else if (w2parent.options.autoFit == "allColumn") {
                                    width = cellWidth;
                                    if (autofitColumnRemainder > 0) {
                                        width += 1;
                                    }
                                    w2parent.maxViewCellWidthTotal += width;
                                    w2parent.colWidthInfo[i2 - axisAreaTh.length] = width;
                                }
                            } else {
                                if (w2parent.options.autoFit == "allColumn" && i2 < (w2parent.pivotDOM.colGroup.length - 10) + addedCell) {
                                    w2parent.colWidthInfo[i2 - axisAreaTh.length] = cellWidth;
                                }
                            }
                            $("#" + w2parent.id + "_col" + i2).width(width);
                            w2parent.pivotWidth += width;
                        }
                    }
                    var tableContent = $("#" + w2parent.id + "_pvtTable"); // column resize 기능을 위해 추가함
                    tableContent.css("table-layout", "fixed"); // table-layout:fixed로 설정한 다음
                    var tableWidth = Math.ceil(w2parent.maxViewCellWidthTotal + axisAreaSize.width);
                    if (w2parent.options.autoFit == "allColumn") {
                        var minWidth = parseInt(w2parent.options.autoFitMinWidth);
                        if (!isNaN(minWidth)) {
                            tableContent.width(Math.max(minWidth, tableWidth) - (scrollY.is(':visible') ? scrollY.width() : 0));
                        } else {
                            tableContent.width(tableWidth);
                        }
                    } else {
                        tableContent.width(tableWidth); // table 전체 width를 설정해야 colgroup을 통한 가로길이 조절이 가능하다.  
                    }
                    if (w2parent.options.autoFit == "allColumn") {
                        if (contentWidth < Math.ceil(w2parent.maxViewCellWidthTotal + axisAreaSize.width + grandTotalSize.width)) {
                            scrollX.show();
                            var bodyHeight = render.height() - containerHeader.outerHeight(true) - scrollX[0].offsetHeight;
                            containerBody.height(bodyHeight);
                            containerContent.height(bodyHeight);
                            $("#" + w2parent.id + "_container_scrollX_barX").css("width", Math.ceil(w2parent.maxViewCellWidthTotal + axisAreaSize.width + grandTotalSize.width));
                            viewSize.height -= scrollX[0].offsetHeight;
                        } else {
                            scrollX.hide();
                            var bodyHeight = render.height() - containerHeader.outerHeight(true);
                            containerBody.height(bodyHeight);
                            if (w2parent.options.editablePivotMode == "true") {
                                bodyHeight -= scrollX[0].offsetHeight;
                            }
                            containerContent.height(bodyHeight);
                        }
                    }
                    w2parent.viewTotalCellWidth = Math.ceil(contentWidth - axisAreaSize.width);
                    w2parent.viewTotalCellHeight = Math.ceil(bodyHeight - axisAreaSize.height);
                    w2parent.viewedRowNum = Math.floor(w2parent.viewTotalCellHeight / cellSize.height);
                    w2parent.viewedColNum = Math.floor(w2parent.viewTotalCellWidth / cellSize.width);
                    var overlapedCellHeight = w2parent.viewTotalCellHeight % cellSize.height;
                    var contentHeight = bodyHeight;
                    if (w2parent.options.editablePivotMode == "true") {
                        if (overlapedCellHeight == 0) {
                            contentHeight = bodyHeight - cellSize.height + 5;
                        } else {
                            contentHeight = overlapedCellHeight > 4 ? (bodyHeight - overlapedCellHeight + 4) : (bodyHeight - cellSize.height);
                        }
                    }
                    containerContent.height(contentHeight);
                    w2parent.cellHeight = cellSize.height;
                    w2parent.cellWidth = cellSize.width;
                    if (WebSquare.util.isSafari()) {
                        WebSquare.cssStyleSheet.changeRule(".type8", "minWidth", "0");
                    }
                    //불필요한 pivot Data 초기화
                    this.rowKeyStrings = {};
                    this.colKeyStrings = {};
                } catch (e) {}
            };
            PivotData.prototype.resizeContentAreaAfterSetColumnWidth = function(result, w2parent) {
                try {
                    var table = $(result);
                    var render = $(w2parent.render);
                    var oneCell = w2parent.pivotDOM.tdMap[0][0] ? $(w2parent.pivotDOM.tdMap[0][0]) : undefined;
                    var containerHeader = $("#" + w2parent.id + "_container_header");
                    var tableHeaderTrList = table[0].getElementsByClassName('w2editablePivot_contentTable_header_TR');
                    var tableContentTrThList = table[0].getElementsByClassName('w2editablePivot_contentTable_content_TR')[0].getElementsByClassName('type7');
                    var viewSize = {
                        width: Math.floor(render[0].getBoundingClientRect().width),
                        height: Math.floor((render[0].getBoundingClientRect().height - containerHeader.outerHeight(true)))
                    }
                    var axisAreaSize = {
                        width: 0,
                        height: 0
                    }
                    for (var i = 0; i < tableHeaderTrList.length; i++) {
                        axisAreaSize.height += $(tableHeaderTrList[i])[0].getBoundingClientRect().height;
                    }
                    for (var i = 0; i < tableContentTrThList.length; i++) {
                        axisAreaSize.width += $(tableContentTrThList[i])[0].getBoundingClientRect().width;
                    }
                    var grandTotalSize = {
                        width: 0,
                        height: 0
                    }
                    if (w2parent.options.showGrandTotal) {
                        if (this.valuesAxis == 'col') {
                            if (w2parent.options.grandTotalDisplayType === "both" || w2parent.options.grandTotalDisplayType === "vertical") {
                                grandTotalSize.width += this.valAttrs.length * w2parent.cellWidth;
                            }
                            if (w2parent.options.grandTotalDisplayType !== "vertical") {
                                grandTotalSize.height += w2parent.cellHeight;
                            }
                        } else {
                            grandTotalSize.width += w2parent.cellWidth;
                            grandTotalSize.height += this.valAttrs.length * w2parent.cellHeight;
                        }
                    }
                    // 각 column width 값 저장
                    w2parent.totalDataCellWidth = 0;
                    var addedCell = 0;
                    if (w2parent.options.showGrandTotal) {
                        if (w2parent.options.grandTotalDisplayType == "horizontal") {
                            addedCell = 0;
                        } else {
                            addedCell = 1;
                            if (this.valuesAxis == "col") {
                                addedCell = this.valAttrs.length;
                            }
                        }
                    }
                    for (var j = 0; j < this.colKeys.length + addedCell; j++) {
                        w2parent.totalDataCellWidth += w2parent.colWidthInfo[j];
                    }
                    var tableSize = {
                        width: Math.ceil(w2parent.totalDataCellWidth + axisAreaSize.width + grandTotalSize.width),
                        height: Math.ceil((this.rowKeys.length * w2parent.cellHeight) + axisAreaSize.height + grandTotalSize.height)
                    }
                    var scrollY = $("#" + w2parent.id + "_container_scrollY");
                    var scrollX = $("#" + w2parent.id + "_container_scrollX");
                    var containerBody = $("#" + w2parent.id + "_container_body");
                    var containerContent = $("#" + w2parent.id + "_container_content");
                    if (viewSize.width < tableSize.width) {
                        scrollX.show();
                        var bodyHeight = render.height() - containerHeader.outerHeight(true) - scrollX[0].offsetHeight;
                        containerBody.height(bodyHeight);
                        if (w2parent.options.editablePivotMode != "true") {
                            containerContent.height(bodyHeight);
                        }
                        $("#" + w2parent.id + "_container_scrollX_barX").css("width", tableSize.width);
                        viewSize.height -= scrollX[0].offsetHeight;
                    } else {
                        scrollX.hide();
                        var bodyHeight = render.height() - containerHeader.outerHeight(true);
                        containerBody.height(bodyHeight);
                        if (w2parent.options.editablePivotMode != "true") {
                            containerContent.height(bodyHeight);
                        }
                    }
                    var contentWidth = 0;
                    if (viewSize.height < tableSize.height) {
                        scrollY.show();
                        contentWidth = render.width() - scrollY.width() - 1; //scrollY 사라지는 문제로 1px 줄임
                        containerContent.width(contentWidth);
                        $("#" + w2parent.id + "_container_scrollY_barY").css("height", tableSize.height);
                    } else {
                        scrollY.hide();
                        contentWidth = render.width(); //render[0].getBoundingClientRect().width;
                        containerContent.width(contentWidth);
                        $("#" + w2parent.id + "_container_scrollY_barY").css("height", tableSize.height);
                    }
                    /*var axisAreaTh = tableHeaderTrList[tableHeaderTrList.length-1].childNodes;
                    for(var i = 0; i < axisAreaTh.length; i++) {
                      $("#" + w2parent.id + "_col" + i ).width(axisAreaTh[i].getBoundingClientRect().width);
                    }
                    for(var i2 = axisAreaTh.length; i2 < w2parent.pivotDOM.colGroup.length; i2++) {
                      $("#" + w2parent.id + "_col" + i2 ).width(w2parent.cellWidth);
                    }*/
                    w2parent.viewTotalCellWidth = Math.ceil(contentWidth - axisAreaSize.width);
                    w2parent.viewTotalCellHeight = Math.ceil(bodyHeight - axisAreaSize.height);
                    //w2parent.viewedRowNum = Math.floor(w2parent.viewTotalCellHeight/cellSize.height);
                    //w2parent.viewedColNum = Math.floor(w2parent.viewTotalCellWidth/cellSize.width);
                    //containerContent.height(viewSize.height - w2parent.viewTotalCellHeight%cellSize.height + 2);
                } catch (e) {}
            };
            return PivotData;
        })();
        /*
        Default Renderer for hierarchical table layout
         */
        pivotTableRenderer = function(pivotData, opts, w2parent) {
            var aggregator, c, colAttrs, colKey, colKeys, defaults, i, j, k, r, result, rowAttrs, rowKey, rowKeys, spanSize, td, th, totalAggregator, tr, txt, val, x, idx;
            var flatColKey, flatRowKey;
            defaults = {
                localeStrings: {
                    totals: "Totals"
                },
                attributeLabels: {}
            };
            opts = $.extend(defaults, opts);
            w2parent.opts = opts;
            colAttrs = pivotData.colAttrs;
            rowAttrs = pivotData.rowAttrs;
            rowKeys = pivotData.getRowKeys();
            colKeys = pivotData.getColKeys();
            longestWidthInColKey = pivotData.getLongestTestWidthColKey();
            longestWidthInRowKey = pivotData.getLongestTestWidthRowKey();
            realRowIndexByRowKeys = pivotData.getRealRowIndexByRowKeys();
            realRowIndexByColKeys = pivotData.getRealRowIndexByColKeys();
            if (colAttrs.length === 0 && colKeys.length > 0) {
                colAttrs = _.flatten(colKeys);
            }
            result = [];
            var tableID = w2parent.id + "_pvtTable";
            resultStr = "<table id='" + tableID + "' class='w2editablePivot_ pivotTable pvtTable' ";
            resultStr += "data-numrows='" + rowKeys.length + "' ";
            resultStr += "data-numcols='" + colKeys.length + "' ";
            resultStr += ">";
            result.push(resultStr); // close
            spanSize = function(arr, i, j) {
                var l, len, n, noDraw, ref, ref1, stop, x;
                if (i !== 0) {
                    noDraw = true;
                    for (x = l = 0, ref = j; 0 <= ref ? l <= ref : l >= ref; x = 0 <= ref ? ++l : --l) {
                        if (arr[i - 1][x] !== arr[i][x]) {
                            noDraw = false;
                        }
                    }
                    if (noDraw) {
                        return -1;
                    }
                }
                len = 0;
                while (i + len < arr.length) {
                    stop = false;
                    for (x = n = 0, ref1 = j; 0 <= ref1 ? n <= ref1 : n >= ref1; x = 0 <= ref1 ? ++n : --n) {
                        if (arr[i][x] !== arr[i + len][x]) {
                            stop = true;
                        }
                    }
                    if (stop) {
                        break;
                    }
                    len++;
                }
                return len;
            };
            rowColSpanSize = function(arr, j) {
                var l, len, n, noDraw, ref, ref1, stop, x;
                len = 0;
                while (j + len < arr.length) {
                    stop = false;
                    for (x = n = 0, ref1 = j; 0 <= ref1 ? n <= ref1 : n >= ref1; x = 0 <= ref1 ? ++n : --n) {
                        if (arr[j] !== arr[j + len]) {
                            stop = true;
                        }
                    }
                    if (stop) {
                        break;
                    }
                    len++;
                }
                return j + len === arr.length ? len + 1 : 1;
            };
            rowColSpanSize2 = function(arr, i, j) {
                var l, len, n, noDraw, ref, ref1, stop, x;
                var up = arr[i - 1] ? arr[i - 1][j] : "";
                var down = arr[i + 1] ? arr[i + 1][j] : "";
                var spanCheck = false;
                if (i !== 0) {
                    if (down && (down !== arr[i][j] || up !== arr[i][j])) {
                        spanCheck = true;
                    } else if (up !== arr[i][j]) {
                        spanCheck = true;
                    }
                } else {
                    if (down && down !== arr[i][j]) {
                        spanCheck = true;
                    }
                }
                if (!spanCheck && (arr[i][j] === arr[i][j - 1])) {
                    return -1;
                }
                if (spanCheck) {
                    var len = 1;
                    var keyLen = arr[i].length;
                    for (var k = j + 1; k < keyLen; k++) {
                        if (arr[i][j] === arr[i][k]) {
                            len++;
                        }
                    }
                    return len === keyLen ? len : 1;
                } else {
                    return 1;
                }
            };
            w2parent.spanSize = spanSize;
            w2parent.totalCellX = colKeys.length;
            w2parent.totalCellY = rowKeys.length;
            if (w2parent.options.cols.length == 0 || w2parent.options.rows.length == 0) {
                w2parent.fixedCellX = rowAttrs.length;
            } else {
                w2parent.fixedCellX = rowAttrs.length + 1;
            }
            w2parent.fixedCellY = colAttrs.length + 1;
            var hiddenCellNum = 10;
            if (w2parent.options.autoFit) {
                //w2parent.maxViewCellX = w2parent.totalCellX + rowAttrs.length + 1;
                w2parent.maxViewCellX = w2parent.totalCellX + 10;
            } else {
                var tempWidth = w2parent.options.cellMinWidth ? w2parent.options.cellMinWidth : w2parent.options.defalutCellWidth;
                w2parent.maxViewCellX = Math.ceil((w2parent.render.clientWidth - (w2parent.fixedCellX * tempWidth)) / tempWidth) + 10;
            }
            w2parent.maxViewCellY = Math.ceil((w2parent.render.clientHeight - (30 * 4) - (w2parent.fixedCellY * w2parent.options.cellMinHeight)) / w2parent.options.cellMinHeight) + 10;
            w2parent.firstRowStr = [];
            if (w2parent.options.showZero == true) {
                var firstRowKey = Object.keys(pivotData.tree)[0];
                if (firstRowKey) {
                    var firstColKey = Object.keys(pivotData.tree[firstRowKey])[0];
                    var tempAggregator = pivotData.tree[firstRowKey][firstColKey];
                }
            }
            var skipHeaderSumX = false;
            var colGroup = "<colgroup>";
            for (var i = 0; i < w2parent.fixedCellX + w2parent.maxViewCellX; i++) {
                colGroup += "<col id='" + w2parent.id + "_col" + i + "'>";
            }
            colGroup += "</colgroup>";
            result.push(colGroup);
            var theadStr = "";
            theadStr += "<thead id='";
            theadStr += w2parent.id + "_head_table";
            theadStr += "' class='";
            theadStr += "w2editablePivot_head_table'>";
            result.push(theadStr);
            var headerTextAlignStr = (w2parent.options.headerTextAlign == "") ? "" : "style='text-Align:" + w2parent.options.headerTextAlign + "' ";
            if (!(this.cols.length == 1 && this.cols[0] == w2parent.options.grandTotalName)) {
                for (j in colAttrs) {
                    // ==============================================================================================
                    // ========================================= headerX ============================================
                    // ==============================================================================================
                    if (!hasProp.call(colAttrs, j)) continue;
                    c = colAttrs[j];
                    result.push("<tr class='w2editablePivot_contentTable_header_TR' >");
                    if (parseInt(j) === 0 && rowAttrs.length !== 0) {
                        if (!(rowAttrs.length == 1 && rowAttrs[0] === w2parent.options.grandTotalName) && !(colAttrs.length == 1 && colAttrs[0] === w2parent.options.grandTotalName)) {
                            var thStr = "<th colspan='";
                            thStr += rowAttrs.length;
                            thStr += "' rowspan='";
                            thStr += colAttrs.length;
                            thStr += "' class='pivotemptycolheader'></th>";
                            result.push(thStr);
                        }
                    }
                    if (!(colAttrs.length == 1 && colAttrs[0] === w2parent.options.grandTotalName)) {
                        var thStr = "<th " + headerTextAlignStr + "class='type1 pvtAxisLabel' data-axis='col:" + j + ":" + (opts.attributeLabels[c] || c) + "'>";
                        if (c == w2parent.options.grandTotalName) {
                            thStr += (opts.attributeLabels[c] || c);
                        } else {
                            thStr += opts.attributeLabels[c];
                        }
                        thStr += "</th>";
                        result.push(thStr)
                    }
                    var drawedHeaderColumnX = 0;
                    var tempColKeys = JSON.parse(JSON.stringify(colKeys));
                    if (!(this.cols.length == 1 && this.cols[0] == w2parent.options.grandTotalName)) {
                        for (i in colKeys) {
                            if (!hasProp.call(colKeys, i)) continue;
                            colKey = colKeys[i];
                            flatColKey = colKey.join(String.fromCharCode(0));
                            if (parseInt(i) < w2parent.options.fixedColumn) {
                                x = 1;
                                //tempColKeys.shift();
                                var fixedColumnColKeys = tempColKeys.slice(0, w2parent.options.fixedColumn);
                                x = spanSize(fixedColumnColKeys, parseInt(i), parseInt(j));
                                if (x !== -1) {
                                    if (drawedHeaderColumnX < w2parent.maxViewCellX) {
                                        var tempX = drawedHeaderColumnX + x;
                                        if (w2parent.maxViewCellX < tempX) {
                                            x -= (tempX - w2parent.maxViewCellX);
                                            drawedHeaderColumnX += x;
                                        } else {
                                            drawedHeaderColumnX = tempX;
                                        }
                                    } else {
                                        skipHeaderSumX = true;
                                        break;
                                    }
                                    var thStr = "";
                                    if (parseInt(i) == w2parent.options.fixedColumn - x) {
                                        thStr += "<th " + headerTextAlignStr + "class='type2 pvtColLabel w2editablePivot_fixedColumnCell fixedCell' data-coordinate='" + j + "," + i + "'colspan='" + x + "'";
                                    } else {
                                        thStr += "<th " + headerTextAlignStr + "class='type2 pvtColLabel fixedCell' data-coordinate='" + j + "," + i + "'colspan='" + x + "'";
                                    }
                                    if (parseInt(j) === colAttrs.length - 1 && rowAttrs.length !== 0) {
                                        thStr += " rowspan='2' ";
                                    }
                                    thStr += ">";
                                    var label = (opts.attributeLabels[colKey[j]] || colKey[j]);
                                    /*if (drawedHeaderColumnX + x >= w2parent.maxViewCellX) {
                                        thStr += longestWidthInColKey[parseInt(j)];
                                    } else*/
                                    {
                                        if (this.headerDataNameFormatter) {
                                            thStr += this.headerDataNameFormatter(realRowIndexByColKeys[flatColKey], colAttrs[parseInt(j)], label, w2parent.data[realRowIndexByColKeys[flatColKey]]);
                                        } else {
                                            thStr += label;
                                        }
                                    }
                                    thStr += "</th>";
                                    result.push(thStr);
                                }
                            } else {
                                x = spanSize(tempColKeys, parseInt(i) /* - w2parent.options.fixedColumn*/ , parseInt(j));
                                if (x !== -1) {
                                    if (drawedHeaderColumnX < w2parent.maxViewCellX) {
                                        var tempX = drawedHeaderColumnX + x;
                                        if (w2parent.maxViewCellX < tempX) {
                                            x -= (tempX - w2parent.maxViewCellX);
                                            drawedHeaderColumnX += x;
                                        } else {
                                            drawedHeaderColumnX = tempX;
                                        }
                                    } else {
                                        skipHeaderSumX = true;
                                        break;
                                    }
                                    var thStr = "";
                                    thStr += "<th " + headerTextAlignStr + "class='type2 pvtColLabel' data-coordinate='" + j + "," + i + "'colspan='" + x + "'";
                                    if (parseInt(j) === colAttrs.length - 1 && rowAttrs.length !== 0) {
                                        thStr += " rowspan='2' ";
                                    }
                                    thStr += ">";
                                    var label = (opts.attributeLabels[colKey[j]] || colKey[j]);
                                    if (w2parent.options.autoFit) {
                                        thStr += label;
                                    } else {
                                        /*if (drawedHeaderColumnX + x >= w2parent.maxViewCellX) {
                                            thStr += longestWidthInColKey[parseInt(j)];
                                        } else*/
                                        {
                                            if (this.headerDataNameFormatter) {
                                                thStr += this.headerDataNameFormatter(realRowIndexByColKeys[flatColKey], colAttrs[parseInt(j)], label), w2parent.data[realRowIndexByColKeys[flatColKey]];
                                            } else {
                                                thStr += label;
                                            }
                                        }
                                    }
                                    thStr += "</th>";
                                    result.push(thStr);
                                }
                            }
                        }
                    }
                    // ==============================================================================================
                    // ===================================== headerX GrandTotal =====================================
                    // ==============================================================================================
                    if (!skipHeaderSumX && w2parent.options.showGrandTotal == true && (w2parent.options.grandTotalDisplayType == 'both' || w2parent.options.grandTotalDisplayType == 'vertical')) {
                        if (parseInt(j) === 0) {
                            if (pivotData.valuesAxis === 'col') {
                                if (pivotData.colAttrs.length > 0) {
                                    for (k = 0; k < pivotData.valAttrs.length; k++) {
                                        var thStr = "<th " + headerTextAlignStr + "class='type3 pvtTotalLabel' rowspan='";
                                        thStr += colAttrs.length + (rowAttrs.length === 0 ? 0 : 1)
                                        thStr += "' >";
                                        thStr += (opts.attributeLabels[pivotData.valAttrs[k]] || pivotData.valAttrs[k]);
                                        thStr += "</th>";
                                        result.push(thStr);
                                    }
                                }
                            } else {
                                if (pivotData.colAttrs.length > 0) {
                                    var thStr = "<th " + headerTextAlignStr + "class='type4 pvtTotalLabel' rowspan='";
                                    thStr += colAttrs.length + (rowAttrs.length === 0 ? 0 : 1);
                                    thStr += "' >";
                                    thStr += w2parent.options.allTotalName ? w2parent.options.allTotalName : opts.localeStrings.totals;
                                    thStr += "</th>";
                                    result.push(thStr);
                                }
                            }
                        }
                    }
                    result.push("</tr>");
                }
            }
            if (rowAttrs.length !== 0 && rowAttrs[0] !== w2parent.options.grandTotalName) {
                // ==============================================================================================
                // ==================================== PIVOT STATUS HEADER =====================================
                // ==============================================================================================
                result.push("<tr class='w2editablePivot_contentTable_header_TR'>");
                for (i in rowAttrs) {
                    if (!hasProp.call(rowAttrs, i)) continue;
                    if ((rowAttrs.length == 1 && rowAttrs[0] === w2parent.options.grandTotalName) /* || (colAttrs.length == 1 && colAttrs[0] === w2parent.options.grandTotalName)*/ ) {
                        continue;
                    }
                    r = rowAttrs[i];
                    var className = (rowAttrs[i] == w2parent.options.grandTotalName) ? "type5 grandTotalAxis pvtAxisLabel" : "type5 pvtAxisLabel";
                    var thStr = "<th " + headerTextAlignStr + "class='" + className + "' data-axis='row:" + i + ":" + (opts.attributeLabels[r] || r) + "'>";
                    //thStr += (opts.attributeLabels[r]||r)
                    if (opts.attributeLabels[r] == undefined) {
                        thStr += r;
                    } else {
                        thStr += (opts.attributeLabels[r])
                    }
                    thStr += "</th>";
                    result.push(thStr)
                }
                //var thStr = "<th ";
                if (!(rowAttrs.length == 1 && rowAttrs[0] === w2parent.options.grandTotalName) /*&& !(colAttrs.length == 1 && colAttrs[0] === w2parent.options.grandTotalName)*/ ) {
                    //thStr += " class='type6 pvtTotalLabel' >";
                    //thStr += (r!==w2parent.options.grandTotalName&&pivotData.valuesAxis==='col'&&pivotData.valAttrs[0]? opts.attributeLabels[pivotData.valAttrs[0]]:opts.localeStrings.totals);
                    if ( /*colAttrs.length == 1 && */ colAttrs[0] === w2parent.options.grandTotalName) {
                        for (var kk = 0; kk < pivotData.valAttrs.length; kk++) {
                            //var thStr = "<th class='type2 pvtAxisLabel' data-axis='col:"+j+":"+opts.attributeLabels[pivotData.valAttrs[kk]]+"'>";
                            var thStr = "<th " + headerTextAlignStr + "class='type2 pvtAxisLabel' data-axis='col:" + kk + ":" + opts.attributeLabels[pivotData.valAttrs[kk]] + "'colspan='1'>";
                            thStr += opts.attributeLabels[pivotData.valAttrs[kk]]
                            thStr += "</th>";
                            result.push(thStr);
                        }
                    } else {
                        result.push("<th class='pivotemptyrowheader'></th>");
                    }
                }
                //else {
                //  thStr += "></th>";
                //}
                //result.push(thStr);
                result.push("</tr>");
            }
            result.push("</thead>");
            var tbodyStr = "";
            tbodyStr += "<tbody id='";
            tbodyStr += w2parent.id + "_body_table";
            tbodyStr += "' class='";
            tbodyStr += "w2editablePivot_body_table'>";
            result.push(tbodyStr);
            if (pivotData.valAttrs) {
                var skipSumY = false;
                var skipColumnSumX = false;
                var tempRowKeys = JSON.parse(JSON.stringify(rowKeys));
                var tempStr;
                for (var i = 0; i < rowKeys.length; i++) { // rowkey
                    if (w2parent.maxViewCellY < parseInt(i)) {
                        skipSumY = true;
                        break;
                    }
                    if (!hasProp.call(rowKeys, i)) continue;
                    if (parseInt(i) < w2parent.options.fixedRow) {
                        result.push("<tr class='w2editablePivot_contentTable_content_TR'>"); // close
                        rowKey = rowKeys[i];
                        flatRowKey = rowKey.join(String.fromCharCode(0));
                        tempRowKeys.shift();
                        for (j in rowKey) {
                            // ==============================================================================================
                            // ========================================== headerY ===========================================
                            // ==============================================================================================
                            if (!hasProp.call(rowKey, j)) continue;
                            txt = rowKey[j];
                            x = 1;
                            if (x !== -1) {
                                var thStr;
                                if (parseInt(i) == (w2parent.options.fixedRow - 1)) {
                                    thStr = "<th " + headerTextAlignStr + "class='type7 pvtRowLabel w2editablePivot_fixedRowCell fixedCell' rowspan='" + x + "' ";
                                } else {
                                    thStr = "<th " + headerTextAlignStr + "class='type7 pvtRowLabel' rowspan='" + x + "' ";
                                }
                                if (parseInt(j) === rowAttrs.length - 1 && colAttrs.length !== 0) {
                                    thStr += " colspan='2' ";
                                }
                                thStr += "> "
                                var label = (opts.attributeLabels[txt] || txt);
                                if (this.headerDataNameFormatter) {
                                    thStr += this.headerDataNameFormatter(realRowIndexByRowKeys[flatRowKey], rowAttrs[j], label, w2parent.data[realRowIndexByRowKeys[flatRowKey]]);
                                } else {
                                    thStr += label;
                                }
                                thStr += "</th>";
                                result.push(thStr);
                            }
                        }
                        // 데이터 셀 X축
                        var drawedColumnX = 0;
                        for (j in colKeys) { // colkey
                            var isDisabledCell = false;
                            // ==============================================================================================
                            // ======================================= pivotDataCell ========================================
                            // ==============================================================================================                
                            if (!hasProp.call(colKeys, j)) continue;
                            if (drawedColumnX < w2parent.maxViewCellX) {
                                drawedColumnX++;
                            } else {
                                skipColumnSumX = true;
                                break;
                            }
                            colKey = colKeys[j];
                            aggregator = pivotData.getAggregator(rowKey, colKey);
                            val = aggregator.value();
                            var bodyTextAlignStr = (w2parent.options.bodyTextAlign == "") ? "" : "style='text-Align:" + w2parent.options.bodyTextAlign + "' ";
                            var tdStr = "<td " + bodyTextAlignStr + "class='w2editablePivot_cell type8 ";
                            if (parseInt(i) == (w2parent.options.fixedRow - 1)) {
                                if (parseInt(j) < (w2parent.options.fixedColumn)) {
                                    if (parseInt(j) == (w2parent.options.fixedColumn - 1)) {
                                        tdStr += "w2editablePivot_fixedRowColumnCell ";
                                    } else {
                                        tdStr += "w2editablePivot_fixedRowCell ";
                                    }
                                } else {
                                    tdStr += "w2editablePivot_fixedRowCell ";
                                }
                            } else {
                                if (parseInt(j) == (w2parent.options.fixedColumn - 1)) {
                                    tdStr += "w2editablePivot_fixedColumnCell ";
                                }
                            }
                            if (val === null) {
                                if (w2parent.options.editablePivotMode == "true") {
                                    tdStr += "w2editablePivot_cellDisabled ";
                                }
                                isDisabledCell = true;
                            }
                            tdStr += "pvtVal fixedCell row" + i + " col" + j + "' data-value='" + val + "' data-coordinate='" + i + "," + j + "'>";
                            if (w2parent.options.useNumberFormat == true) {
                                val = w2parent._pivotNumberFormatter(val);
                            } else {
                                if (w2parent.options.editablePivotMode != "true" && w2parent.options.showZero == true && (val == null || val == "" || val == 0)) {
                                    val = 0;
                                    //aggregator가 없어 format이 없으므로 aggregator가 존재하는 format을 사용
                                    //aggregator = tempAggregator;
                                } else {
                                    val = aggregator.format(val);
                                }
                            }
                            if (this.numberFormatter) {
                                var info = {
                                    rowIndex: i,
                                    colIndex: j,
                                    value: val
                                }
                                val = this.numberFormatter(info);
                            }
                            tdStr += val || "";
                            tdStr += "</td>";
                            result.push(tdStr);
                        }
                        // ==============================================================================================
                        // ==================================== pivotDataCell GTotal ====================================
                        // ==============================================================================================
                        if (!skipColumnSumX && w2parent.options.showGrandTotal == true && (w2parent.options.grandTotalDisplayType == 'both' || w2parent.options.grandTotalDisplayType == 'vertical')) {
                            w2parent.isDrawedGTX = true;
                            var totalYTextAlignStr = (w2parent.options.totalYTextAlign == "") ? "" : "style='text-Align:" + w2parent.options.totalYTextAlign + "' ";
                            if (pivotData.valuesAxis === 'col') {
                                if ((pivotData.colAttrs.length > 0 && !(pivotData.colAttrs.length == 1 && pivotData.colAttrs[0] === w2parent.options.grandTotalName)) || (
                                        pivotData.colAttrs.length === 0 &&
                                        pivotData.rowAttrs.length === 1 &&
                                        pivotData.valAttrs.length === 1
                                    )) {
                                    for (k = 0; k < pivotData.valAttrs.length; k++) {
                                        totalAggregator = pivotData.getAggregator(rowKey, [], pivotData.valAttrs[k]);
                                        val = totalAggregator.value();
                                        if (parseInt(i) == (w2parent.options.fixedRow - 1)) {
                                            var tdStr = "<td " + totalYTextAlignStr + "class='w2editablePivot_cell type9 w2editablePivot_fixedRowCell pvtTotal rowTotal' data-attr='" + pivotData.valAttrs[k] + "' >";
                                        } else {
                                            var tdStr = "<td " + totalYTextAlignStr + "class='w2editablePivot_cell type9 pvtTotal rowTotal' data-attr='" + pivotData.valAttrs[k] + "' >";
                                        }
                                        if (w2parent.options.useNumberFormat == true) {
                                            val = w2parent._pivotNumberFormatter(val);
                                        } else {
                                            val = totalAggregator.format(val);
                                        }
                                        if (this.numberFormatter) {
                                            var info = {
                                                rowIndex: i,
                                                colIndex: j,
                                                value: val
                                            }
                                            val = this.numberFormatter(info);
                                        }
                                        tdStr += val;
                                        tdStr += "</td>";
                                        result.push(tdStr);
                                    }
                                }
                            } else {
                                if (!(colAttrs.length == 1 && colAttrs[0] === w2parent.options.grandTotalName)) {
                                    idx = _.indexOf(rowAttrs, w2parent.options.grandTotalName);
                                    totalAggregator = pivotData.getAggregator(rowKey, [], idx === -1 ? pivotData.valAttrs[0] : rowKey[idx]);
                                    val = totalAggregator.value();
                                    if (parseInt(i) == (w2parent.options.fixedRow - 1)) {
                                        var tdStr = "<td " + totalYTextAlignStr + "class='w2editablePivot_cell type10 w2editablePivot_fixedRowCell pvtTotal rowTotal' data-value='" + val + "' data-for='row" + i + "' >";
                                    } else {
                                        var tdStr = "<td " + totalYTextAlignStr + "class='w2editablePivot_cell type10 pvtTotal rowTotal' data-value='" + val + "' data-for='row" + i + "' >";
                                    }
                                    if (w2parent.options.useNumberFormat == true) {
                                        val = w2parent._pivotNumberFormatter(val);
                                    } else {
                                        val = totalAggregator.format(val);
                                    }
                                    if (this.numberFormatter) {
                                        var info = {
                                            rowIndex: i,
                                            colIndex: j,
                                            value: val
                                        }
                                        val = this.numberFormatter(info);
                                    }
                                    tdStr += val;
                                    tdStr += "</td>";
                                    result.push(tdStr);
                                }
                            }
                        }
                        result.push("</tr>");
                    } else {
                        rowKey = rowKeys[i];
                        flatRowKey = rowKey.join(String.fromCharCode(0));
                        result.push("<tr class='w2editablePivot_contentTable_content_TR'>"); // close
                        var colSpanIndex = 0;
                        var tempRowData = rowKey.slice(0, rowKey.length);
                        var dataHeader_data = WebSquare.util.getDataPrefix("header-data");
                        for (j in rowKey) {
                            // ==============================================================================================
                            // ========================================== headerY ===========================================
                            // ==============================================================================================
                            if (!hasProp.call(rowKey, j)) continue;
                            txt = rowKey[j];
                            x = spanSize(tempRowKeys, parseInt(i) - w2parent.options.fixedRow, parseInt(j));
                            if (w2parent.options.headerYMerge) {
                                if (x !== -1 && tempRowData.length > 0) {
                                    var rowColSpan = rowColSpanSize(tempRowData, 0);
                                    tempRowData = tempRowData.slice(rowColSpan);
                                    if (rowColSpan !== -1) {
                                        if (tempRowData.length > 0) {
                                            var thStr = "<th " + headerTextAlignStr + "class='type7 pvtRowLabel' " + dataHeader_data + "='row:" + j + "'" + " rowspan='" + x + "' ";
                                            thStr += " colspan='" + rowColSpan + "' ";
                                        } else {
                                            if ((tempRowData.length === 0 || parseInt(j) === rowAttrs.length - 1) && colAttrs.length !== 0) {
                                                if ((this.rows.length == 1 && this.rows[0] == w2parent.options.grandTotalName) || (this.cols.length == 1 && this.cols[0] == w2parent.options.grandTotalName)) {
                                                    var thStr = "<th " + headerTextAlignStr + "class='type7 pvtAxisLabel'" + "data-axis='col:" + j + ":" + txt + "'" + "rowspan='" + x + "' ";
                                                    thStr += " colspan='" + 1 + "' ";
                                                } else {
                                                    var thStr = "<th " + headerTextAlignStr + "class='type7 pvtRowLabel' " + dataHeader_data + "='row:" + j + "'" + "rowspan='" + x + "' ";
                                                    thStr += " colspan='" + rowColSpan + "' ";
                                                }
                                            }
                                        }
                                        thStr += "> "
                                        var label = (opts.attributeLabels[txt] || txt);
                                        var tempStr;
                                        if (this.headerDataNameFormatter) {
                                            tempStr = this.headerDataNameFormatter(realRowIndexByRowKeys[flatRowKey], rowAttrs[j], label, w2parent.data[realRowIndexByRowKeys[flatRowKey]]);
                                        } else {
                                            tempStr = label;
                                        }
                                        if (parseInt(i) == 0 && !(longestWidthInRowKey[parseInt(j)] === null || longestWidthInRowKey[parseInt(j)] === undefined)) {
                                            if (pivotData.getStringWidth(longestWidthInRowKey[parseInt(j)]) < pivotData.getStringWidth(tempStr.toString())) {
                                                thStr += tempStr;
                                            } else {
                                                thStr += longestWidthInRowKey[parseInt(j)];
                                            }
                                            w2parent.firstRowStr.push(tempStr);
                                        } else {
                                            thStr += tempStr;
                                        }
                                        thStr += "</th>";
                                        result.push(thStr);
                                    }
                                } else {
                                    tempRowData = tempRowData.slice(1);
                                }
                            } else {
                                if (x !== -1) {
                                    var thStr = "<th " + headerTextAlignStr + "class='type7 pvtRowLabel' " + dataHeader_data + "='row:" + j + "'" + " rowspan='" + x + "' ";
                                    if (parseInt(j) === rowAttrs.length - 1 && colAttrs.length !== 0) {
                                        if ((this.rows.length == 1 && this.rows[0] == w2parent.options.grandTotalName) || (this.cols.length == 1 && this.cols[0] == w2parent.options.grandTotalName)) {
                                            var thStr = "<th " + headerTextAlignStr + "class='type7 pvtAxisLabel'" + "data-axis='col:" + j + ":" + txt + "'" + "rowspan='" + x + "' ";
                                            thStr += " colspan='1' ";
                                        } else {
                                            var thStr = "<th " + headerTextAlignStr + "class='type7 pvtRowLabel' " + dataHeader_data + "='row:" + j + "'" + "rowspan='" + x + "' ";
                                            thStr += " colspan='2' ";
                                        }
                                    }
                                    thStr += "> "
                                    var label = (opts.attributeLabels[txt] || txt);
                                    var tempStr;
                                    if (this.headerDataNameFormatter) {
                                        tempStr = this.headerDataNameFormatter(realRowIndexByRowKeys[flatRowKey], rowAttrs[j], label, w2parent.data[realRowIndexByRowKeys[flatRowKey]]);
                                    } else {
                                        tempStr = label;
                                    }
                                    if (parseInt(i) == 0 && !(longestWidthInRowKey[parseInt(j)] === null || longestWidthInRowKey[parseInt(j)] === undefined)) {
                                        if (pivotData.getStringWidth(longestWidthInRowKey[parseInt(j)]) < pivotData.getStringWidth(tempStr.toString())) {
                                            thStr += tempStr;
                                        } else {
                                            thStr += longestWidthInRowKey[parseInt(j)];
                                        }
                                        w2parent.firstRowStr.push(tempStr);
                                    } else {
                                        thStr += tempStr;
                                    }
                                    thStr += "</th>";
                                    result.push(thStr);
                                }
                            }
                        }
                        // 데이터 셀 X축
                        var colKeysIndex = 0;
                        var drawedColumnX = 0;
                        for (j in colKeys) { // colkey
                            var isDisabledCell = false;
                            // ==============================================================================================
                            // ======================================= pivotDataCell ========================================
                            // ==============================================================================================              
                            if (!hasProp.call(colKeys, j)) continue;
                            if (drawedColumnX < w2parent.maxViewCellX) {
                                drawedColumnX++;
                            } else {
                                skipColumnSumX = true;
                                break;
                            }
                            colKey = colKeys[j];
                            aggregator = pivotData.getAggregator(rowKey, colKey);
                            val = aggregator.value();
                            var bodyTextAlignStr = (w2parent.options.bodyTextAlign == "") ? "" : "style='text-Align:" + w2parent.options.bodyTextAlign + "' ";
                            var tdStr = "<td " + bodyTextAlignStr + "class='w2editablePivot_cell type8 ";
                            if (parseInt(j) < w2parent.options.fixedColumn) {
                                if (parseInt(j) == w2parent.options.fixedColumn - 1) {
                                    tdStr += "w2editablePivot_fixedColumnCell ";
                                }
                                tdStr += "fixedCell ";
                            }
                            if (val === null) {
                                if (w2parent.options.editablePivotMode == "true") {
                                    tdStr += "w2editablePivot_cellDisabled ";
                                }
                                isDisabledCell = true;
                            }
                            tdStr += "pvtVal row" + i + " col" + j + "' data-value='" + val + "' data-coordinate='" + i + "," + j + "'>";
                            if (this.aggregatorName != "List Unique Values") {
                                if (w2parent.options.useNumberFormat == true) {
                                    val = w2parent._pivotNumberFormatter(val);
                                } else {
                                    if (w2parent.options.editablePivotMode != "true" && w2parent.options.showZero == true && (val == null || val == "" || val == 0)) {
                                        val = 0;
                                        //aggregator가 없어 format이 없으므로 aggregator가 존재하는 format을 사용
                                        //aggregator = tempAggregator;
                                    } else {
                                        if (w2parent.options.valueDataType == "number") {
                                            val = aggregator.format(val);
                                        }
                                    }
                                }
                                if (this.numberFormatter) {
                                    var info = {
                                        rowIndex: i,
                                        colIndex: j,
                                        value: val,
                                        rowKey: rowKey,
                                        colKey: colKey
                                    }
                                    val = this.numberFormatter(info);
                                }
                            }
                            tdStr += val || "";
                            tdStr += "</td>";
                            result.push(tdStr);
                            colKeysIndex++;
                        }
                        // ==============================================================================================
                        // ==================================== pivotDataCell GTotal ====================================
                        // ==============================================================================================
                        if (!skipColumnSumX && w2parent.options.showGrandTotal == true && (w2parent.options.grandTotalDisplayType == 'both' || w2parent.options.grandTotalDisplayType == 'vertical')) {
                            w2parent.isDrawedGTX = true;
                            var totalYTextAlignStr = (w2parent.options.totalYTextAlign == "") ? "" : "style='text-Align:" + w2parent.options.totalYTextAlign + "' ";
                            if (pivotData.valuesAxis === 'col') {
                                if ((pivotData.colAttrs.length > 0 && !(pivotData.colAttrs.length == 1 && pivotData.colAttrs[0] === w2parent.options.grandTotalName)) || (
                                        pivotData.colAttrs.length === 0 &&
                                        pivotData.rowAttrs.length === 1 &&
                                        pivotData.valAttrs.length === 1
                                    )) {
                                    for (k = 0; k < pivotData.valAttrs.length; k++) {
                                        totalAggregator = pivotData.getAggregator(rowKey, [], pivotData.valAttrs[k]);
                                        val = totalAggregator.value();
                                        var tdStr = "<td " + totalYTextAlignStr + "class='w2editablePivot_cell type9 pvtTotal rowTotal' data-attr='" + pivotData.valAttrs[k] + "' >";
                                        if (w2parent.options.useNumberFormat == true) {
                                            val = w2parent._pivotNumberFormatter(val);
                                        } else {
                                            val = totalAggregator.format(val);
                                        }
                                        if (this.numberFormatter) {
                                            var info = {
                                                rowIndex: i,
                                                value: val,
                                                rowKey: rowKey,
                                                colKey: pivotData.valAttrs[k]
                                            }
                                            val = this.numberFormatter(info);
                                        }
                                        tdStr += val;
                                        tdStr += "</td>";
                                        result.push(tdStr);
                                    }
                                }
                            } else {
                                if (!(colAttrs.length == 1 && colAttrs[0] === w2parent.options.grandTotalName)) {
                                    idx = _.indexOf(rowAttrs, w2parent.options.grandTotalName);
                                    totalAggregator = pivotData.getAggregator(rowKey, [], idx === -1 ? pivotData.valAttrs[0] : rowKey[idx]);
                                    val = totalAggregator.value();
                                    var tdStr = "<td " + totalYTextAlignStr + "class='w2editablePivot_cell type10 pvtTotal rowTotal' data-value='" + val + "' data-for='row" + i + "' >";
                                    if (w2parent.options.useNumberFormat == true) {
                                        val = w2parent._pivotNumberFormatter(val);
                                    } else {
                                        val = totalAggregator.format(val);
                                    }
                                    if (this.numberFormatter) {
                                        var totalString = w2parent.options.allTotalName ? w2parent.options.allTotalName : opts.localeStrings.totals;
                                        var info = {
                                            rowIndex: i,
                                            value: val,
                                            rowKey: rowKey,
                                            colKey: totalString
                                        }
                                        val = this.numberFormatter(info);
                                    }
                                    tdStr += val;
                                    tdStr += "</td>";
                                    result.push(tdStr);
                                }
                            }
                        }
                        result.push("</tr>");
                    }
                }
            }
            result.push("</tbody>");
            // ==============================================================================================
            // ================================== pivotGrandTotal Buttom ====================================
            // ==============================================================================================
            if (!skipSumY && w2parent.options.showGrandTotal == true && (w2parent.options.grandTotalDisplayType == 'both' || w2parent.options.grandTotalDisplayType == 'horizontal')) {
                if (pivotData.valuesAxis === 'col') {
                    if ((pivotData.colAttrs.length > 0 && !(pivotData.rowAttrs.length == 1 && pivotData.rowAttrs[0] == w2parent.options.grandTotalName)) || (
                            pivotData.valAttrs.length === 1 && (
                                pivotData.colAttrs.length === 0 ||
                                pivotData.rowAttrs.length === 0
                            ))) {
                        w2parent.isDrawedGTY = true;
                        result.push(("<tr class='w2editablePivot_contentTable_gtotal_TR' data-attr='GrandTotal'> "));
                        var thStr = "<th " + headerTextAlignStr + "class='type11 pvtTotalLabel' colspan='";
                        if ((pivotData.colAttrs.length == 1 && pivotData.colAttrs[0] === w2parent.options.grandTotalName)) {
                            thStr += (rowAttrs.length);
                        } else {
                            thStr += (rowAttrs.length + (colAttrs.length === 0 ? 0 : 1));
                        }
                        thStr += "'>";
                        var totalString = w2parent.options.allTotalName ? w2parent.options.allTotalName : opts.localeStrings.totals;
                        thStr += totalString;
                        thStr += "</th>";
                        result.push(thStr);
                        idx = _.indexOf(colAttrs, w2parent.options.grandTotalName);
                        // 표측 토탈
                        var isBroken = false;
                        var drawedColumnX = 0;
                        for (j in colKeys) {
                            if (!hasProp.call(colKeys, j)) continue;
                            if (drawedColumnX < w2parent.maxViewCellX) {
                                drawedColumnX++;
                            } else {
                                isBroken = true;
                                break;
                            }
                            colKey = colKeys[j];
                            totalAggregator = pivotData.getAggregator([], colKey, idx === -1 ? pivotData.valAttrs[0] : colKey[idx]);
                            val = totalAggregator.value();
                            var totalXTextAlignStr = (w2parent.options.totalXTextAlign == "") ? "" : "style='text-Align:" + w2parent.options.totalXTextAlign + "' ";
                            var tdStr = "<td " + totalXTextAlignStr + "class='w2editablePivot_cell type12 pvtTotal colTotal' data-value='" + val + "' data-for='col" + j + "'>";
                            if (w2parent.options.useNumberFormat) {
                                val = w2parent._pivotNumberFormatter(val);
                            } else {
                                val = totalAggregator.format(val);
                            }
                            if (this.numberFormatter) {
                                var info = {
                                    colIndex: j,
                                    value: val,
                                    rowKey: totalString,
                                    colKey: colKey
                                }
                                val = this.numberFormatter(info);
                            }
                            tdStr += val;
                            tdStr += "</td>";
                            result.push(tdStr);
                        }
                        if (!isBroken && w2parent.options.grandTotalDisplayType == 'both') {
                            w2parent.isDrawedGTT = true;
                            if (!(pivotData.colAttrs.length == 1 && pivotData.colAttrs[0] === w2parent.options.grandTotalName)) {
                                for (i = 0; i < pivotData.valAttrs.length; i++) {
                                    totalAggregator = pivotData.getAggregator([], [], pivotData.valAttrs[i]);
                                    val = totalAggregator.value();
                                    var totalTextAlignStr = (w2parent.options.totalTextAlign == "") ? "" : "style='text-Align:" + w2parent.options.totalTextAlign + "' ";
                                    var tdStr = "<td " + totalTextAlignStr + "class='type13 pvtGrandTotal' data-attr='" + pivotData.valAttrs[i] + "'>";
                                    val = totalAggregator.format(val);
                                    if (this.numberFormatter) {
                                        var info = {
                                            value: val,
                                            rowKey: totalString,
                                            colKey: pivotData.valAttrs[i]
                                        }
                                        val = this.numberFormatter(info);
                                    }
                                    tdStr += val;
                                    tdStr += "</td>";
                                    result.push(tdStr);
                                }
                            }
                        } else {
                            w2parent.isDrawedGTT = false;
                        }
                        result.push("</tr>");
                    }
                } else {
                    if ((pivotData.rowAttrs.length > 0 && !(pivotData.rowAttrs.length == 1 && pivotData.rowAttrs[0] == w2parent.options.grandTotalName)) || (
                            pivotData.valAttrs.length === 1 && (
                                pivotData.colAttrs.length === 0 ||
                                pivotData.rowAttrs.length === 0
                            ))) {
                        w2parent.isDrawedGTY = true;
                        for (i = 0; i < pivotData.valAttrs.length; i++) {
                            result.push(("<tr class='w2editablePivot_contentTable_gtotal_TR' data-attr='" + pivotData.valAttrs[i] + "'>"));
                            var thStr = "<th " + headerTextAlignStr + "class='type14 pvtTotalLabel' colspan='";
                            if (colAttrs.length == 1 && colAttrs[0] == w2parent.options.grandTotalName) {
                                thStr += rowAttrs.length;
                            } else {
                                thStr += (rowAttrs.length + (colAttrs.length === 0 ? 0 : 1));
                            }
                            thStr += "' >";
                            thStr += (opts.attributeLabels[pivotData.valAttrs[i]] || pivotData.valAttrs[i]);
                            thStr += "</th>";
                            result.push(thStr);
                            var isBroken = false;
                            var drawedColumnX = 0;
                            var totalXTextAlignStr = (w2parent.options.totalXTextAlign == "") ? "" : "style='text-Align:" + w2parent.options.totalXTextAlign + "' ";
                            for (j in colKeys) {
                                if (!hasProp.call(colKeys, j)) continue;
                                if (drawedColumnX < w2parent.maxViewCellX) {
                                    drawedColumnX++;
                                } else {
                                    isBroken = true;
                                    break;
                                }
                                colKey = colKeys[j];
                                totalAggregator = pivotData.getAggregator([], colKey, pivotData.valAttrs[i]);
                                val = totalAggregator.value();
                                if (parseInt(j) < w2parent.options.fixedColumn) {
                                    if (parseInt(j) == w2parent.options.fixedColumn - 1) {
                                        var tdStr = "<td " + totalXTextAlignStr + "class='w2editablePivot_cell type15 w2editablePivot_fixedColumnCell fixedCell pvtTotal colTotal' data-value='" + val + "' data-for='col" + j + "' data-row='" + i + "' data-col='" + j + "' >";
                                    } else {
                                        var tdStr = "<td " + totalXTextAlignStr + "class='w2editablePivot_cell type15 fixedCell pvtTotal colTotal' data-value='" + val + "' data-for='col" + j + "' data-row='" + i + "' data-col='" + j + "' >";
                                    }
                                } else {
                                    var tdStr = "<td " + totalXTextAlignStr + "class='w2editablePivot_cell type15 pvtTotal colTotal' data-value='" + val + "' data-for='col" + j + "' data-row='" + i + "' data-col='" + j + "' >";
                                }
                                if (w2parent.options.useNumberFormat) {
                                    val = w2parent._pivotNumberFormatter(val);
                                } else {
                                    val = totalAggregator.format(val);
                                }
                                if (this.numberFormatter) {
                                    var info = {
                                        colIndex: j,
                                        value: val,
                                        rowKey: pivotData.valAttrs[i],
                                        colKey: colKey
                                    }
                                    val = this.numberFormatter(info);
                                }
                                tdStr += val;
                                tdStr += "</td>";
                                result.push(tdStr);
                            }
                            if (!isBroken && !(colAttrs.length == 1 && colAttrs[0] === w2parent.options.grandTotalName) && w2parent.options.grandTotalDisplayType == 'both') {
                                w2parent.isDrawedGTT = true;
                                totalAggregator = pivotData.getAggregator([], [], pivotData.valAttrs[i]);
                                val = totalAggregator.value();
                                var tdStr = "<td " + totalXTextAlignStr + "class='type16 pvtGrandTotal' data-value='" + val + "' >";
                                if (w2parent.options.useNumberFormat) {
                                    val = w2parent._pivotNumberFormatter(val);
                                } else {
                                    val = totalAggregator.format(val);
                                }
                                if (this.numberFormatter) {
                                    var totalString = w2parent.options.allTotalName ? w2parent.options.allTotalName : opts.localeStrings.totals;
                                    var info = {
                                        value: val,
                                        rowKey: pivotData.valAttrs[i],
                                        colKey: totalString
                                    }
                                    val = this.numberFormatter(info);
                                }
                                tdStr += val;
                                tdStr += "</td>";
                                result.push(tdStr);
                            }
                            result.push("</tr>")
                        }
                    }
                }
            }
            var sum = result.join("");
            var reultHTML = $.parseHTML(sum);
            return reultHTML;
        };
        /*
        Pivot Table core: create PivotData object and call Renderer on it
         */
        $.fn.pivot = function(input, opts, obfuscator_a, obfuscator_b, w2parent) {
            var defaults, e, pivotData, result, originalView;
            w2parent.colWidthInfo = {};
            w2parent.cellWidth = 0;
            var scanWholePivot2 = function() {
                //console.log("scanWholePivot");
                if ($("#" + w2parent.id + "_pvtTable")[0]) {
                    w2parent.pivotDOM.colGroup = $("#" + w2parent.id + "_pvtTable")[0].getElementsByTagName("col");
                    w2parent.pivotDOM.headerMap = $("#" + w2parent.id + "_pvtTable")[0].getElementsByClassName("w2editablePivot_contentTable_header_TR");
                    var trList = $("#" + w2parent.id + "_pvtTable")[0].getElementsByClassName("w2editablePivot_contentTable_content_TR");
                    for (var rIndex = 0; rIndex < trList.length; rIndex++) {
                        var tr = trList[rIndex];
                        var tdList = tr.getElementsByClassName("type8");
                        w2parent.pivotDOM.tdMap[rIndex] = tdList;
                    }
                }
            }
            var scanWholePivot = function() {
                //console.log("scanWholePivot");
                // STEP 2 SCANNING
                // BEGIN OF THE FUNCTION
                w2parent.hasProp = hasProp;
                w2parent.pivotSheetInfo = {
                    headerX: {
                        axis: {
                            rows: [],
                            cols: []
                        },
                        depth: []
                    },
                    headerY: {
                        axis: {
                            vals: []
                        },
                        depth: []
                    }
                };
                w2parent.spanInfo = {
                    "spanX": {
                        depth: {}
                    },
                    "spanY": {
                        depth: {}
                    }
                };
                var pivotData = w2parent.pivotData;
                var colAttrs = pivotData.colAttrs;
                var rowAttrs = pivotData.rowAttrs;
                var valAttrs = pivotData.valAttrs;
                var rowKeys = pivotData.getRowKeys();
                var colKeys = pivotData.getColKeys();
                var spanSize = w2parent.spanSize;
                var pivotTree = pivotData.tree;
                var i = 0;
                var j = 0;
                if (w2parent.options.showZero && w2parent.data.length) {
                    var firstRowKey = Object.keys(pivotTree)[0];
                    if (firstRowKey) {
                        var firstColKey = Object.keys(pivotTree[firstRowKey])[0];
                    }
                }
                var pivotDataArr = [];
                var rowKeysLen = rowKeys.length;
                var colKeysLen = colKeys.length;
                for (var rIndex = 0; rIndex < rowKeysLen; rIndex++) {
                    var flatRowKey = rowKeys[rIndex].join(String.fromCharCode(0));
                    var row = pivotData.tree[flatRowKey];
                    for (var cIndex = 0; cIndex < colKeysLen; cIndex++) {
                        var flatColKey = colKeys[parseInt(cIndex)].join(String.fromCharCode(0));
                        var cell = row[flatColKey];
                        if (cell) {
                            if (w2parent.options.valueDataType == "text") {
                                pivotDataArr.push(cell.value());
                            } else {
                                pivotDataArr.push(cell.format(cell.value()));
                            }
                        } else {
                            if (w2parent.options.showZero) {
                                pivotDataArr.push(pivotData.tree[firstRowKey][firstColKey].format(0));
                            } else {
                                pivotDataArr.push(null);
                            }
                        }
                    }
                }
                w2parent.pivotDataArr = pivotDataArr;
                var rowTotalDataArr = [];
                var colTotalDataArr = [];
                var allTotalDataArr = [];
                if (w2parent.options.showGrandTotal == true) {
                    for (var rIndex = 0; rIndex < rowKeys.length; rIndex++) {
                        var flatRowKey = rowKeys[rIndex].join(String.fromCharCode(0));
                        var rObj = pivotData.rowTotals[flatRowKey];
                        for (var oIndex in rObj) {
                            var aggregator = rObj[oIndex];
                            rowTotalDataArr.push(aggregator.format(aggregator.value()));
                        }
                    }
                    if (pivotData.valuesAxis == "col") {
                        for (var cIndex = 0; cIndex < colKeys.length; cIndex++) {
                            var flatColKey = colKeys[cIndex].join(String.fromCharCode(0));
                            var colTotalObj = pivotData.colTotals[flatColKey];
                            for (var oIndex in colTotalObj) {
                                var aggregator = colTotalObj[oIndex];
                                colTotalDataArr.push(aggregator.format(aggregator.value()));
                            }
                        }
                    } else if (pivotData.valuesAxis == "row") {
                        var totalObject = {};
                        for (var cIndex = 0; cIndex < colKeys.length; cIndex++) {
                            var flatColKey = colKeys[cIndex].join(String.fromCharCode(0));
                            var colTotalObj = pivotData.colTotals[flatColKey];
                            for (var oIndex in colTotalObj) {
                                if (!totalObject[oIndex]) {
                                    totalObject[oIndex] = [];
                                }
                                var aggregator = colTotalObj[oIndex];
                                totalObject[oIndex].push(aggregator.format(aggregator.value()));
                            }
                        }
                        for (var vIndex in totalObject) {
                            colTotalDataArr = colTotalDataArr.concat(totalObject[vIndex]);
                        }
                    }
                    for (var aIndex in pivotData.allTotal) {
                        var aggregator = pivotData.allTotal[aIndex];
                        allTotalDataArr.push(aggregator.format(aggregator.value()));
                    }
                }
                w2parent.rowTotalDataArr = rowTotalDataArr;
                w2parent.colTotalDataArr = colTotalDataArr;
                w2parent.allTotalDataArr = allTotalDataArr;
                var colAttrsLen = colAttrs.length;
                var colKeysLen = colKeys.length;
                for (var j = 0; j < colAttrsLen; j++) {
                    for (var i = 0; i < colKeysLen; i++) {
                        if (!hasProp.call(colKeys, i)) continue;
                        var sSize = spanSize(colKeys, parseInt(i), parseInt(j));
                        var flatColKey = colKeys[i].join(String.fromCharCode(0));
                        if (sSize != -1) {
                            if (!w2parent.spanInfo["spanX"][i]) {
                                w2parent.spanInfo["spanX"][i] = {};
                            }
                            w2parent.spanInfo["spanX"][i][j] = sSize;
                            if (!w2parent.spanInfo["spanX"]["depth"][j]) {
                                w2parent.spanInfo["spanX"]["depth"][j] = {
                                    spanSize: [],
                                    spanColIndexArr: [],
                                    spanColDataArr: []
                                };
                            }
                            w2parent.spanInfo["spanX"]["depth"][j]["spanSize"][i] = sSize;
                            w2parent.spanInfo["spanX"]["depth"][j]["spanColIndexArr"].push(parseInt(i));
                            w2parent.spanInfo["spanX"]["depth"][j]["spanColDataArr"].push(colKeys[i][j]);
                            var jIndex = parseInt(j);
                            if (!w2parent.pivotSheetInfo.headerX.depth[jIndex]) {
                                w2parent.pivotSheetInfo.headerX.depth.push({
                                    spanSizeArr: [],
                                    indexArr: [],
                                    valueArr: []
                                });
                            }
                            if ((colAttrs.length - 1) != jIndex) {
                                w2parent.pivotSheetInfo.headerX.depth[jIndex].spanSizeArr.push(sSize);
                                w2parent.pivotSheetInfo.headerX.depth[jIndex].indexArr.push(parseInt(i));
                            }
                            var str;
                            if (w2parent.headerDataNameFormatter) {
                                str = w2parent.headerDataNameFormatter(realRowIndexByColKeys[flatColKey], colAttrs[parseInt(j)], colKeys[i][j] + "", w2parent.data[realRowIndexByColKeys[flatColKey]]);
                                //불필요한 공백 및 tag 제거
                                str = str.replace(/&nbsp;/g, "");
                                str = str.replace(/<\/?[^>]+(>|$)/g, "");
                                //var div = document.createElement("div");
                                //div.innerHTML = str;
                                //var str = div.textContent || div.innerText || "";
                            } else {
                                str = colKeys[i][j] + "";
                                str = w2parent.opts.attributeLabels[str] || str;
                            }
                            w2parent.pivotSheetInfo.headerX.depth[jIndex].valueArr.push(str);
                        }
                    }
                }
                var rowKeysLen = rowKeys.length;
                for (var i = 0; i < rowKeysLen; i++) {
                    var rowKey = rowKeys[i];
                    var rowKeyLen = rowKey.length;
                    var flatRowKey = rowKeys[i].join(String.fromCharCode(0));
                    var tempRowData = rowKey.slice(0, rowKeyLen);
                    for (var j = 0; j < rowKeyLen; j++) {
                        if (!hasProp.call(rowKeys, i)) continue;
                        var sSize = spanSize(rowKeys, parseInt(i), parseInt(j));
                        var rowColSpan = 1;
                        if (sSize != -1) {
                            rowColSpan = rowColSpanSize(tempRowData, 0);
                            if (!w2parent.spanInfo["spanY"][i]) {
                                w2parent.spanInfo["spanY"][i] = {};
                            }
                            w2parent.spanInfo["spanY"][i][j] = sSize;
                            if (!w2parent.spanInfo["spanY"]["depth"][j]) {
                                w2parent.spanInfo["spanY"]["depth"][j] = {
                                    spanSize: [],
                                    spanRowIndexArr: [],
                                    spanRowDataArr: []
                                };
                            }
                            w2parent.spanInfo["spanY"]["depth"][j]["spanSize"][i] = sSize;
                            w2parent.spanInfo["spanY"]["depth"][j]["spanRowIndexArr"].push(parseInt(i));
                            w2parent.spanInfo["spanY"]["depth"][j]["spanRowDataArr"].push(rowKeys[i][j]);
                            var jIndex = parseInt(j);
                            if (!w2parent.pivotSheetInfo.headerY.depth[jIndex]) {
                                if (w2parent.options.headerYMerge) {
                                    w2parent.pivotSheetInfo.headerY.depth.push({
                                        spanSizeArr: [],
                                        colSpanSizeArr: [],
                                        indexArr: [],
                                        valueArr: []
                                    });
                                } else {
                                    w2parent.pivotSheetInfo.headerY.depth.push({
                                        spanSizeArr: [],
                                        indexArr: [],
                                        valueArr: []
                                    });
                                }
                            }
                            if ((rowKey.length - 1) != jIndex) {
                                w2parent.pivotSheetInfo.headerY.depth[jIndex].spanSizeArr.push(sSize);
                                w2parent.pivotSheetInfo.headerY.depth[jIndex].indexArr.push(parseInt(i));
                                rowColSpan = rowColSpan > 1 ? rowColSpan - 1 : rowColSpan;
                                if (w2parent.options.headerYMerge) {
                                    w2parent.pivotSheetInfo.headerY.depth[jIndex].colSpanSizeArr.push(rowColSpan);
                                }
                            }
                            var str;
                            if (w2parent.headerDataNameFormatter) {
                                var rowKeyStr = (rowKeys[i][j] + "");
                                if (pivotData.rowKeyStrings[j][rowKeyStr]) {
                                    str = pivotData.rowKeyStrings[j][rowKeyStr];
                                } else {
                                    str = w2parent.headerDataNameFormatter(realRowIndexByRowKeys[flatRowKey], rowAttrs[j], rowKeyStr, w2parent.data[realRowIndexByRowKeys[flatRowKey]]);
                                }
                                //불필요한 공백 및 tag 제거
                                str = str.replace(/&nbsp;/g, "");
                                str = str.replace(/<\/?[^>]+(>|$)/g, "");
                                //var div = document.createElement("div");
                                //div.innerHTML = str;
                                //var str = div.textContent || div.innerText || "";
                            } else {
                                str = rowKeys[i][j] + "";
                                str = w2parent.opts.attributeLabels[str] || str;
                            }
                            w2parent.pivotSheetInfo.headerY.depth[jIndex].valueArr.push(str);
                        }
                        tempRowData = tempRowData.slice(rowColSpan);
                    }
                }
                w2parent.mappingDataInfoByKey = pivotData.mappingDataInfoByKey;
                w2parent.mappingDataInfoByRowIndex = pivotData.mappingDataInfoByRowIndex;
                /*w2parent.pivotDOM.colGroup = $("#"+w2parent.id+"_pvtTable")[0].getElementsByTagName("col");
                w2parent.pivotDOM.headerMap = $("#"+w2parent.id+"_pvtTable")[0].getElementsByClassName("w2editablePivot_contentTable_header_TR");

                var trList = $("#"+w2parent.id+"_pvtTable")[0].getElementsByClassName("w2editablePivot_contentTable_content_TR");
                for ( var rIndex=0; rIndex<trList.length; rIndex++){
                  var tr = trList[rIndex];
                  var tdList = tr.getElementsByClassName("type8");
                  w2parent.pivotDOM.tdMap[rIndex] = tdList;
                }*/
                // END OF THE FUNCTION
            }
            opts = opts || {};
            if (!opts.locale) {
                opts.locale = 'ko';
            }
            defaults = {
                cols: [],
                rows: [],
                vals: [],
                valuesAxis: 'row',
                filter: function() {
                    return true;
                },
                aggregatorName: "Count",
                aggregatorList: {},
                sorters: function() {},
                derivedAttributes: {},
                renderer: pivotTableRenderer,
                rendererOptions: {
                    localeStrings: locales[opts.locale]['localeStrings']
                },
                localeStrings: locales[opts.locale]['localeStrings']
            };
            opts = $.extend(defaults, opts);
            if (!opts.aggregator) {
                opts.aggregator = locales[opts.locale]['aggregators'][opts.aggregatorName];
            }
            if (!opts.aggregatorMap) {
                opts.aggregatorMap = _.clone(locales[opts.locale]['aggregators']);
            }
            if (opts.attributeLabels) {
                opts.rendererOptions.attributeLabels = opts.attributeLabels;
            }
            result = null;
            if ((opts.cols && opts.cols.length > 0) || (opts.rows && opts.rows.length > 0) || (opts.vals && opts.vals.length > 0)) {
                opts.aggregators = {};
                opts.allTotals = {};
                for (var i = 0; i < opts.vals.length; i++) {
                    opts.aggregators[opts.vals[i]] = opts.aggregatorList[opts.vals[i]] ? opts.aggregatorMap[opts.aggregatorList[opts.vals[i]]]([opts.vals[i]]) : opts.aggregator([opts.vals[i]]);
                    opts.allTotals[opts.vals[i]] = opts.aggregatorList[opts.vals[i]] ? opts.aggregatorMap[opts.aggregatorList[opts.vals[i]]]([opts.vals[i]]) : opts.aggregator([opts.vals[i]]);
                }
                if (opts.vals.length > 1) {
                    if (opts.valuesAxis === 'row') {
                        if (_.indexOf(opts.rows, w2parent.options.grandTotalName) === -1 && opts.cols[0] != w2parent.options.grandTotalName) {
                            opts.rows.push(w2parent.options.grandTotalName);
                        }
                    } else {
                        if (_.indexOf(opts.cols, w2parent.options.grandTotalName) === -1) {
                            opts.cols.push(w2parent.options.grandTotalName);
                        }
                    }
                }
                try {
                    pivotData = new PivotData(input, opts, w2parent);
                    try {
                        // w2parent.opts = opts;
                        w2parent.pivotData = pivotData;
                        result = opts.renderer(pivotData, opts.rendererOptions, w2parent);
                    } catch (_error) {
                        e = _error;
                        if (typeof console !== "undefined" && console !== null) {
                            console.error(e.stack);
                        }
                        result = $("<span>").html(opts.localeStrings.renderError);
                    }
                } catch (_error) {
                    e = _error;
                    if (typeof console !== "undefined" && console !== null) {
                        console.error(e.stack);
                    }
                    result = $("<span>").html(opts.localeStrings.computeError);
                }
                scanWholePivot();
                //x = this[0];
                //while (x.hasChildNodes()) {
                //  x.removeChild(x.lastChild);
                //}
                if (originalView) {
                    originalView.remove();
                }
                this.html('');
                this.append(result);
                //scanWholePivot();
                scanWholePivot2();
                $(".pvtAxisLabel").sortable({
                    //connectWith: this.find(".pvtAxisContainer")
                    connectWith: this.find(".pvtHorizList")
                });
                // STEP 3
                // scanning virtual pivot dom
                pivotData.resizeContentArea(result[0], w2parent);
            } else {
                this.html('');
            }
            w2parent.createdPivot = true;
            return {
                originalView: originalView
            };
        };
        /*
        Pivot Table UI: calls Pivot Table core above with options set by user
         */
        $.fn.pivotUI = function(input, inputOpts, overwrite, locale, w2parent) {
            var a, attrLength, axisValues, c, colList, defaults, e, existingOpts, fn, i, initialRender, k, l, len1, len2, len3, len4, n, o, opts, pivotTable, q, ref, ref1, ref3, ref4, refresh, refreshDelayed, renderer, rendererControl, shownAttributes, tblCols, tr1, tr2, uiTable, unusedAttrsVerticalAutoOverride, x;
            var valueList, getValueList, getAggregatorList, getAxisItem, setAggregatorName, updateFilter, showFilterList;
            w2parent.createdPivot = false;
            if (overwrite == null) {
                overwrite = false;
            }
            if (locale == null) {
                locale = "en";
            }
            defaults = {
                derivedAttributes: {},
                attributeLabels: {},
                aggregatorName: "Count",
                aggregators: locales[locale].aggregators,
                aggregatorList: {},
                renderers: locales[locale].renderers,
                hiddenAttributes: [],
                menuLimit: 1000,
                cols: [],
                rows: [],
                vals: [],
                exclusions: {},
                inclusions: {},
                unusedAttrsVertical: "",
                autoSortUnusedAttrs: false,
                rendererOptions: {
                    localeStrings: locales[locale].localeStrings
                },
                onRefresh: null,
                filter: function() {
                    return true;
                },
                sorters: function() {},
                localeStrings: locales[locale].localeStrings
            };
            existingOpts = this.data("pivotUIOptions");
            if ((existingOpts == null) || overwrite) {
                opts = $.extend(defaults, inputOpts);
            } else {
                opts = existingOpts;
            }
            if (this.data("sPosition")) {
                this.data("sPosition", null);
            }
            try {
                if (!_.isArray(input) && !_.isObject(input[0])) {
                    input = PivotData.convertToArray(input);
                }
                tblCols = (function() {
                    var ref, results;
                    ref = input[0];
                    results = [];
                    for (k in ref) {
                        if (!hasProp.call(ref, k)) continue;
                        results.push(k);
                    }
                    return results;
                })();
                if (tblCols.length == 0 && opts.pivotColumns.length > 0) {
                    tblCols = opts.pivotColumns;
                }
                ref = opts.derivedAttributes;
                for (c in ref) {
                    if (!hasProp.call(ref, c)) continue;
                    if ((indexOf.call(tblCols, c) < 0)) {
                        tblCols.push(c);
                    }
                }
                axisValues = {};
                for (l = 0, len1 = tblCols.length; l < len1; l++) {
                    x = tblCols[l];
                    axisValues[x] = {};
                }
                PivotData.forEachRecord(input, opts.derivedAttributes, function(record, rowIndex) {
                    var base, results, v;
                    results = [];
                    for (k in record) {
                        if (!hasProp.call(record, k)) continue;
                        v = record[k];
                        if (!(opts.filter(record))) {
                            continue;
                        }
                        if (v == null) {
                            v = "null";
                        }
                        if ((base = axisValues[k])[v] == null) {
                            base[v] = 0;
                        }
                        results.push(axisValues[k][v]++);
                    }
                    return results;
                });
                // STEP 1
                // CONTAINER를 만든다.
                this.html('');
                var containerHeader = $("<div>")
                    .attr("id", w2parent.id + "_container_header")
                    .addClass("w2editablePivot_container_header");
                var containerBody = $("<div>")
                    .attr("id", w2parent.id + "_container_body")
                    .addClass("w2editablePivot_container_body");
                var containerContent = $("<div>")
                    .attr("id", w2parent.id + "_container_content")
                    .addClass("w2editablePivot_container_content")
                    .appendTo(containerBody);
                var containerScrollY = $("<div>")
                    .attr("id", w2parent.id + "_container_scrollY")
                    .attr("data-parent", w2parent.id)
                    .addClass("w2editablePivot_container_scrollY")
                    .appendTo(containerBody);
                var barY = $("<div>")
                    .attr("id", w2parent.id + "_container_scrollY_barY")
                    .addClass("w2editablePivot_container_scrollY_barY")
                    .appendTo(containerScrollY);
                var containerScrollX = $("<div>")
                    .attr("id", w2parent.id + "_container_scrollX")
                    .attr("data-parent", w2parent.id)
                    .addClass("w2editablePivot_container_scrollX");
                var barX = $("<div>")
                    .attr("id", w2parent.id + "_container_scrollX_barX")
                    .addClass("w2editablePivot_container_scrollX_barX")
                    .appendTo(containerScrollX);
                var noResultMessage;
                var noresultLayer = w2parent.getElementById(w2parent.id + "_noresult");
                if (noresultLayer) {
                    noResultMessage = noresultLayer;
                } else {
                    noResultMessage = $("<div>")
                        .attr("id", w2parent.id + "_noresult")
                        .attr("style", 'display:none;position:absolute;' + w2parent.options.noResultMessageStyle)
                        .text(w2parent.options.noReslutMessage)
                        .addClass(w2parent.options.noResultMessageClass);
                }
                // ContainerBody height
                this.append(containerHeader);
                this.append(containerBody);
                this.append(containerScrollX);
                this.append(noResultMessage);
                var pivotTable = containerContent;
                w2parent.pivotDOM.pivotDIV = pivotTable[0];
                w2parent.scrollY.scrollY_AREA = containerScrollY[0];
                w2parent.scrollY.scrollY_BAR = barY[0];
                w2parent.scrollX.scrollX_AREA = containerScrollX[0];
                w2parent.scrollX.scrollX_BAR = barX[0];
                containerScrollX[0].addEventListener("scroll", function(e) {
                    var w2parent = WebSquare.core.getComponentById(this.getAttribute("data-parent"));
                    w2parent.handleScrollX(e);
                });
                containerScrollY[0].addEventListener("scroll", function(e) {
                    var w2parent = WebSquare.core.getComponentById(this.getAttribute("data-parent"));
                    w2parent.handleScrollY(e);
                });
                // uiTable = $("<table>").attr("id", w2parent.id+"_containerTable").addClass("w2editablePivot_containerTable");
                // var colGroup = $("<colgroup>");
                // colGroup.append($("<col style='max-width:"+ w2parent.render.clientWidth +"px'>"));
                // uiTable.append(colGroup);
                // rendererControl = $('<td class="alignCenter w2editablePivot_renderer">');
                // renderer = $("<select>").addClass('pvtRenderer').appendTo(rendererControl).bind("change", function() {
                //   return refresh();
                // });
                // ref1 = opts.renderers;
                // for (x in ref1) {
                //   if (!hasProp.call(ref1, x)) continue;
                //   $("<option>").val(x).html(x).appendTo(renderer);
                // }
                colList = $("<div>").attr("id", w2parent.id + "_container_header_hiddenList").addClass('w2editablePivot_container_header_hiddenList pvtAxisContainer pvtUnused');
                shownAttributes = (function() {
                    var len2, n, results;
                    results = [];
                    for (n = 0, len2 = tblCols.length; n < len2; n++) {
                        c = tblCols[n];
                        if (indexOf.call(opts.hiddenAttributes, c) < 0 && indexOf.call(opts.pivotColumns, c) != -1) {
                            results.push(c);
                        }
                    }
                    return results;
                })();
                unusedAttrsVerticalAutoOverride = false;
                if (opts.unusedAttrsVertical === "auto") {
                    attrLength = 0;
                    for (n = 0, len2 = shownAttributes.length; n < len2; n++) {
                        a = shownAttributes[n];
                        attrLength += a.length;
                    }
                    unusedAttrsVerticalAutoOverride = attrLength > 120;
                }
                if (opts.unusedAttrsVertical === true || unusedAttrsVerticalAutoOverride) {
                    // rendererControl.attr( "colspan", 4 );
                    colList.addClass('pvtVertList');
                } else {
                    // rendererControl.attr( "rowspan", 4 );
                    colList.addClass('pvtHorizList');
                }
                opts.exclusionList = _.reduce(opts.exclusions, function(memo, list, key) {
                    memo[key] = _.reduce(list, function(memo2, value) {
                        memo2[value] = value;
                        return memo2;
                    }, {});
                    return memo;
                }, {});
                opts.inclusionList = _.reduce(opts.inclusions, function(memo, list, key) {
                    memo[key] = _.reduce(list, function(memo2, value) {
                        memo2[value] = value;
                        return memo2;
                    }, {});
                    return memo;
                }, {});
                getValueList = function getValueList(c) {
                    var keys, btns, checkContainer, ref2, o, len3, k, v,
                        filterItem;
                    keys = (function() {
                        var results;
                        results = [];
                        for (k in axisValues[c.c]) {
                            results.push(k);
                        }
                        return results;
                    })();
                    valueList.html('');
                    valueList.append($("<h4>").text(c.d + " (" + keys.length + ")"));
                    if (keys.length > opts.menuLimit) {
                        valueList.append($("<p>").html(opts.localeStrings.tooMany));
                    } else {
                        btns = $("<p>").appendTo(valueList);
                        btns.append($("<button>", {
                            type: "button"
                        }).html(opts.localeStrings.selectAll).bind("click", function() {
                            return valueList.find("input:visible").prop("checked", true);
                        }));
                        btns.append($("<button>", {
                            type: "button"
                        }).html(opts.localeStrings.selectNone).bind("click", function() {
                            return valueList.find("input:visible").prop("checked", false);
                        }));
                        btns.append($("<input>", {
                            type: "text",
                            placeholder: opts.localeStrings.filterResults,
                            "class": "pvtSearch"
                        }).bind("keyup", function() {
                            var filter;
                            filter = $(this).val().toLowerCase();
                            return valueList.find('.pvtCheckContainer p').each(function() {
                                var testString;
                                testString = $(this).text().toLowerCase().indexOf(filter);
                                if (testString !== -1) {
                                    return $(this).show();
                                } else {
                                    return $(this).hide();
                                }
                            });
                        }));
                        checkContainer = $("<div>").addClass("pvtCheckContainer").appendTo(valueList);
                        ref2 = keys.sort(getSort(opts.sorters, c.c));
                        for (o = 0, len3 = ref2.length; o < len3; o++) {
                            k = ref2[o];
                            v = axisValues[c.c][k];
                            filterItem = $("<label>");
                            $("<input>").attr("type", "checkbox").addClass('pvtFilter').attr("checked", opts.exclusionList[c.c] ? !opts.exclusionList[c.c][k] : true).data("filter", [c.c, k]).appendTo(filterItem);
                            filterItem.append($("<span>").html(k));
                            filterItem.append($("<span>").text(" (" + v + ")"));
                            checkContainer.append($("<p>").append(filterItem));
                        }
                    }
                };
                getAggregatorList = function getAggregatorList(c) {
                    var $aggregator;
                    valueList.html('');
                    valueList.append($("<h4>").text(c.d));
                    $aggregator = $('<select class="pvtAggregator"></select>');
                    _.each(opts.aggregators, function(aggrFunc, aggrName) {
                        $aggregator.append($('<option>').val(aggrName).html(aggrName));
                    });
                    valueList.append($aggregator);
                    if (opts.aggregatorList[c.c]) {
                        $aggregator.val(opts.aggregatorList[c.c]);
                    } else if (opts.aggregatorName != null) {
                        $aggregator.val(opts.aggregatorName);
                    }
                };
                setAggregatorName = function setAggregatorName($ele, attrName) {
                    // $ele[0].firstChild.insertData( 0, opts.aggregatorList[attrName] ? opts.aggregatorList[attrName] + ': ' : opts.aggregatorName + ': ' );
                };
                updateFilter = function updateFilter() {
                    var isExcluded,
                        openEle,
                        textNode,
                        valueListLength = 0,
                        $checkboxList,
                        aggregatorValue = valueList.find('.pvtAggregator').val(),
                        openItem = valueList.data('openItem');
                    if (openItem.area === 'pvtVals') {
                        openEle = valueList.prev('table').find('div.' + openItem.area + ' li.' + openItem.item + ' .pvtAttr');
                        textNode = openEle[0].firstChild;
                        textNode.replaceData(0, textNode.data.indexOf(': ') + 2, aggregatorValue + ': ');
                        opts.aggregatorList[openEle.data('attrName')] = aggregatorValue;
                        return valueList.toggle(0, refresh);
                    } else {
                        $checkboxList = valueList.find("[type='checkbox']");
                        $checkboxList.each(function(idx, ele) {
                            var $ele = $(ele),
                                filter;
                            if (!filter) {
                                filter = $ele.data('filter');
                            }
                            if (idx === 0) {
                                opts.exclusionList[filter[0]] = {};
                            }
                            if ($(ele).is(":checked")) {
                                valueListLength += 1;
                            } else {
                                isExcluded = true;
                                opts.exclusionList[filter[0]][filter[1]] = filter[1];
                            }
                        });
                        openEle = valueList.prev('table').find('div.' + openItem.area + ' li.' + openItem.item);
                        if (isExcluded) {
                            openEle.addClass("pvtFilteredAttribute");
                        } else {
                            openEle.removeClass("pvtFilteredAttribute");
                        }
                        if (valueListLength > opts.menuLimit) {
                            return valueList.toggle();
                        } else {
                            return valueList.toggle(0, refresh);
                        }
                    }
                };
                showFilterList = function showFilterList(e) {
                    var attrName,
                        areaClass,
                        openItem = {},
                        popPosition,
                        $wrapper,
                        positionGap,
                        $target = $(e.target);
                    if ($target.closest('div').hasClass('pvtUnused')) {
                        return false;
                    } else {
                        if ($target.hasClass('pvtTriangle')) {
                            $target = $target.parent('span');
                        }
                        attrName = $target.data('attrName');
                        openItem.item = $target.closest('li').attr('class');
                        openItem.item = openItem.item.split(/\s+/)[0];
                        areaClass = $target.closest('div').attr('class');
                        if (areaClass.indexOf('pvtVals') > -1) {
                            openItem.area = 'pvtVals';
                        } else if (areaClass.indexOf('pvtCols') > -1) {
                            openItem.area = 'pvtCols';
                        } else if (areaClass.indexOf('pvtRows') > -1) {
                            openItem.area = 'pvtRows';
                        }
                        valueList.data('openItem', openItem);
                        if (openItem.area === 'pvtVals') {
                            getAggregatorList({
                                c: attrName,
                                d: opts.attributeLabels[attrName] || attrName
                            });
                        } else {
                            getValueList({
                                c: attrName,
                                d: opts.attributeLabels[attrName]
                                //d: opts.attributeLabels[attrName] || attrName
                            });
                        }
                        $("<p>").appendTo(valueList).append($("<button>", {
                            type: "button"
                        }).text("적용").bind("click", updateFilter)).append($("<button>", {
                            type: "button"
                        }).text("닫기").bind("click", function() {
                            valueList.toggle(0);
                        }));
                        popPosition = $target.find('.pvtTriangle').position();
                        $wrapper = $target.closest('.w2editablePivot');
                        positionGap = $wrapper.width() - (popPosition.left + valueList.width() + 20);
                        if (positionGap < 0) {
                            popPosition.left += positionGap;
                        }
                        positionGap = $wrapper.height() - (popPosition.top + valueList.height() + 20);
                        if (positionGap < 0) {
                            popPosition.top += positionGap;
                        }
                        valueList.css({
                            left: popPosition.left,
                            top: popPosition.top
                        }).show();
                    }
                };
                fn = function(c) {
                    var attrElem, triangleLink, vFlag, dragToHiddenFlag;
                    vFlag = true;
                    dragToHiddenFlag = true;
                    for (var cIndex = 0; cIndex < defaults.cols.length; cIndex++) {
                        if (defaults.cols[cIndex] == c.c) {
                            vFlag = false;
                            break;
                        }
                    }
                    for (var rIndex = 0; rIndex < defaults.rows.length; rIndex++) {
                        if (defaults.rows[rIndex] == c.c) {
                            vFlag = false;
                            break;
                        }
                    }
                    for (var cIndex = 0; cIndex < defaults.rejectDragtoHiddenColumns.length; cIndex++) {
                        if (defaults.rejectDragtoHiddenColumns[cIndex] == c.c) {
                            dragToHiddenFlag = false;
                            break;
                        }
                    }
                    for (var rIndex = 0; rIndex < defaults.rejectDragtoHiddenColumns.length; rIndex++) {
                        if (defaults.rejectDragtoHiddenColumns[rIndex] == c.c) {
                            dragToHiddenFlag = false;
                            break;
                        }
                    }
                    if (vFlag) {
                        triangleLink = $("<span>").addClass('pvtTriangle').html(" &#x25BE;").bind("click", showFilterList).hide();
                        attrElem = $("<li>").addClass("w2editablePivot_container_header_pvtAttr").addClass("axis_" + i).addClass('valueItem').append($("<span>").addClass('pvtAttr').text(c.d).data("attrName", c.c).append(triangleLink));
                    } else {
                        triangleLink = $("<span>").addClass('pvtTriangle').html(" &#x25BE;").bind("click", showFilterList).hide();
                        attrElem = $("<li>").attr('draggable', 'false').addClass("w2editablePivot_container_header_pvtAttr").addClass("axis_" + i).append($("<span>").addClass('pvtAttr').text(c.d).data("attrName", c.c).append(triangleLink));
                        attrElem.bind("dblclick", showFilterList);
                    }
                    if (dragToHiddenFlag == false) {
                        attrElem.addClass('rejectDragToHidden');
                    }
                    if (c.d == '') {
                        attrElem.find('.pvtAttr').addClass('pvtAttrNoName');
                    }
                    colList.append(attrElem);
                    return attrElem;
                };
                for (i in shownAttributes) {
                    if (!hasProp.call(shownAttributes, i)) continue;
                    c = shownAttributes[i];
                    c = {
                        c: c,
                        d: opts.attributeLabels[c]
                        //d: opts.attributeLabels[c] || c
                    };
                    fn(c);
                }
                adjustLayout = function() {
                    var render = $(w2parent.render);
                    var containerHeader = $("#" + w2parent.id + "_container_header");
                    var containerBody = $("#" + w2parent.id + "_container_body");
                    var containerContent = $("#" + w2parent.id + "_container_content");
                    var containerScrollY = $("#" + w2parent.id + "_container_scrollY");
                    var containerScrollX = $("#" + w2parent.id + "_container_scrollX");
                    var hideHeaderList = 0;
                    var headerList = w2parent.render.getElementsByClassName('w2editablePivot_container_header')[0].getElementsByTagName('div');
                    var hideHeaderList = w2parent.options.hiddenHeaderList.split(w2parent.seperator);
                    for (var i = 0; i < headerList.length; i++) {
                        var hDiv = headerList[i];
                        $(hDiv).width(render.width() - 12);
                        for (var j = 0; j < hideHeaderList.length; j++) {
                            switch (hideHeaderList[j]) {
                                case "col":
                                    if (hDiv.className.indexOf("colAxisList") > 0) {
                                        $(hDiv).hide();
                                    }
                                    break;
                                case "row":
                                    if (hDiv.className.indexOf("rowAxisList") > 0) {
                                        $(hDiv).hide();
                                    }
                                    break;
                                case "val":
                                    if (hDiv.className.indexOf("showedValueList") > 0) {
                                        $(hDiv).hide();
                                    }
                                    break;
                                case "hidden":
                                    if (hDiv.className.indexOf("hiddenList") > 0) {
                                        $(hDiv).hide();
                                    }
                                    break;
                            }
                        }
                    }
                    var headerHeight = 0;
                    if (opts.unusedAttrsVertical === true || unusedAttrsVerticalAutoOverride) {
                        var headerHeight = ($("#" + w2parent.id + "_container_header_hiddenList")[0].getBoundingClientRect().height * 4);
                    } else {
                        var headerHeight =
                            ($("#" + w2parent.id + "_container_header_hiddenList")[0].getBoundingClientRect().height) +
                            ($("#" + w2parent.id + "_container_header_showedValueList")[0].getBoundingClientRect().height) +
                            ($("#" + w2parent.id + "_container_header_colAxisList")[0].getBoundingClientRect().height) +
                            ($("#" + w2parent.id + "_container_header_rowAxisList")[0].getBoundingClientRect().height);
                    }
                    containerHeader.height(headerHeight);
                    var bodyHeight = render.height() - containerHeader.outerHeight(true) - containerScrollX.height();
                    containerBody.height(bodyHeight);
                    var contentWidth = render.width() - containerScrollY.width();
                    containerContent.width(contentWidth);
                }
                var colAxisList = $("<div>").attr("id", w2parent.id + "_container_header_colAxisList").addClass('w2editablePivot_container_header_colAxisList pvtAxisContainer pvtHorizList pvtCols').attr("data-type", "colAxisList");
                var rowAxisList = $("<div>").attr("id", w2parent.id + "_container_header_rowAxisList").addClass('w2editablePivot_container_header_rowAxisList pvtAxisContainer pvtHorizList pvtRows').attr("data-type", "rowAxisList");
                var showedValueList = $("<div>").attr("id", w2parent.id + "_container_header_showedValueList").addClass("w2editablePivot_container_header_showedValueList pvtAxisContainer pvtHorizList pvtVals");
                var hiddenList = colList;
                var headerListOrder = w2parent.options.headerListOrder.split(w2parent.seperator);
                var hideHeaderList = w2parent.options.hiddenHeaderList.split(w2parent.seperator);
                if (headerListOrder.length == 4) {
                    for (var i = 0; i < headerListOrder.length; i++) {
                        switch (headerListOrder[i]) {
                            case "col":
                                if (_.contains(hideHeaderList, "col")) {
                                    containerHeader.prepend(colAxisList);
                                } else {
                                    colAxisList.appendTo(containerHeader);
                                }
                                break;
                            case "row":
                                if (_.contains(hideHeaderList, "row")) {
                                    containerHeader.prepend(rowAxisList);
                                } else {
                                    rowAxisList.appendTo(containerHeader);
                                }
                                break;
                            case "val":
                                if (_.contains(hideHeaderList, "val")) {
                                    containerHeader.prepend(showedValueList);
                                } else {
                                    showedValueList.appendTo(containerHeader);
                                }
                                break;
                            case "hidden":
                                if (_.contains(hideHeaderList, "hidden")) {
                                    containerHeader.prepend(hiddenList);
                                } else {
                                    hiddenList.appendTo(containerHeader);
                                }
                                break;
                        }
                    }
                } else {
                    colAxisList.appendTo(containerHeader);
                    rowAxisList.appendTo(containerHeader);
                    if (opts.unusedAttrsVertical === true || unusedAttrsVerticalAutoOverride) {
                        containerHeader.find('div:nth-child(1)').prepend(rendererControl);
                        containerHeader.find('div:nth-child(2)').prepend($("<div style='min-width: 170px;'>").addClass("w2editablePivot_container_header_verticalValueList pvtAxisContainer pvtVertList pvtVals"));
                        containerHeader.find('div:nth-child(2)').prepend(colList);
                    } else {
                        containerHeader.prepend(showedValueList);
                        containerHeader.prepend(hiddenList);
                    }
                }
                valueList = $("<div>").addClass('pvtFilterBox').hide();
                this.append(valueList);
                adjustLayout();
                getAxisItem = function getAxisItem(x, shownAttributes, isVal) {
                    var $axisItem = null;
                    $axisItem = this.find(".axis_" + ($.inArray(x, shownAttributes)));
                    if (isVal) {
                        // setAggregatorName( $axisItem.find('.pvtAttr'), x );
                        $axisItem.find('.pvtTriangle').hide();
                    } else {
                        if (opts.exclusionList[x] && !_.isEmpty(opts.exclusionList[x])) {
                            $axisItem.addClass('pvtFilteredAttribute');
                        }
                        $axisItem.find('.pvtTriangle').show();
                    }
                    return $axisItem;
                };
                ref3 = opts.cols;
                for (o = 0, len3 = ref3.length; o < len3; o++) {
                    x = ref3[o];
                    this.find(".pvtCols").append(getAxisItem.call(this, x, shownAttributes, false));
                }
                ref4 = opts.rows;
                for (q = 0, len4 = ref4.length; q < len4; q++) {
                    x = ref4[q];
                    this.find(".pvtRows").append(getAxisItem.call(this, x, shownAttributes, false));
                }
                ref4 = opts.vals;
                for (q = 0, len4 = ref4.length; q < len4; q++) {
                    x = ref4[q];
                    this.find(".pvtVals").append(getAxisItem.call(this, x, shownAttributes, true));
                }
                if (opts.rendererName != null) {
                    this.find(".pvtRenderer").val(opts.rendererName);
                }
                initialRender = true;
                refreshDelayed = (function(_this) {
                    return function(e, ui, w2parent, moveAttrToContentBody) {
                        var exclusions, inclusions, pivotUIOptions, pvtVals, subopts, unusedAttrsContainer;
                        var sPosition, attrElem, valList, droped, dest, unUsedArea, $fields, $field, i, selectorStr, textNode;
                        var valuesAxis = opts.valuesAxis || w2parent.options.valuesAxis;
                        subopts = {
                            derivedAttributes: opts.derivedAttributes,
                            localeStrings: opts.localeStrings,
                            rendererOptions: opts.rendererOptions || {},
                            sorters: opts.sorters,
                            numberFormatter: opts.numberFormatter,
                            headerDataNameFormatter: opts.headerDataNameFormatter,
                            cols: [],
                            rows: [],
                            vals: []
                        };
                        _this.find(".pvtRows li span.pvtAttr").each(function() {
                            return subopts.rows.push($(this).data("attrName"));
                        });
                        _this.find(".pvtCols li span.pvtAttr").each(function() {
                            return subopts.cols.push($(this).data("attrName"));
                        });
                        _this.find(".pvtVals li span.pvtAttr").each(function() {
                            return subopts.vals.push($(this).data("attrName"));
                        });
                        if (moveAttrToContentBody == true) {
                            var $tableContentBody = $(e.originalEvent.target).closest('th');
                            var destAxisInfo;
                            var $pvtAttr = ui.item.find('.pvtAttr');
                            var attrName = $pvtAttr.data('attrName');
                            subopts.cols = opts.cols;
                            subopts.rows = opts.rows;
                            if ($tableContentBody.attr('data-axis')) {
                                destAxisInfo = $tableContentBody.attr('data-axis').split(':');
                            }
                            if ($tableContentBody.hasClass('type1')) {
                                subopts.cols.splice(destAxisInfo[1], 0, attrName);
                                w2parent.options.cols = subopts.cols.join();
                            } else if ($tableContentBody.hasClass('type5')) {
                                subopts.rows.splice(destAxisInfo[1], 0, attrName);
                                w2parent.options.rows = subopts.rows.join();
                            } else if ($tableContentBody.hasClass('type2') && subopts.cols[0] == w2parent.options.grandTotalName) {
                                subopts.cols = [attrName];
                            } else if ($tableContentBody.hasClass('type7') && subopts.rows[0] == w2parent.options.grandTotalName) {
                                subopts.rows = [attrName];
                            }
                            delete opts.inclusions[attrName];
                            delete opts.exclusions[attrName];
                            delete opts.inclusionList[attrName];
                            delete opts.exclusionList[attrName];
                            for (var i = 0; i < w2parent.columnInfoList.length; i++) {
                                var colInfo = w2parent.columnInfoList[i];
                                if (colInfo.id == attrName) {
                                    colInfo.inclusions = null;
                                }
                            }
                        }
                        /*if(subopts.rows.length == 0 && subopts.cols.length != 0) {
                          subopts.rows = subopts.vals;
                        }*/
                        if (ui && ui.item.find('.pvtAttr').length) {
                            var valueIndex = _.indexOf(subopts.rows, opts.grandTotalName);
                            if (subopts.rows.length > 1 && valueIndex >= 0 && subopts.vals.length > 1 && (valueIndex != subopts.rows.length - 1)) {
                                var valueAttr = subopts.rows.splice(valueIndex, 1);
                                subopts.rows.push(valueAttr[0]);
                                var divs = document.getElementsByClassName("dummyAttr");
                                divs[0].parentNode.appendChild(divs[0]);
                            }
                            valueIndex = _.indexOf(subopts.cols, opts.grandTotalName);
                            if (subopts.cols.length > 1 && valueIndex >= 0 && subopts.vals.length > 1 && (valueIndex != subopts.cols.length - 1)) {
                                var valueAttr = subopts.cols.splice(valueIndex, 1);
                                subopts.cols.push(valueAttr[0]);
                                var divs = document.getElementsByClassName("dummyAttr");
                                divs[0].parentNode.appendChild(divs[0]);
                            }
                            w2parent.options.cols = subopts.cols.join(",");
                            w2parent.options.rows = subopts.rows.join(",");
                            w2parent.options.vals = subopts.vals.join(",");
                        }
                        sPosition = _this.data('sPosition');
                        if (subopts.vals.length > 1 || (subopts.vals.length == 1 && (subopts.rows.length == 0 || subopts.cols.length == 0))) {
                            if (!sPosition) {
                                if (subopts.rows.length == 0 || subopts.cols.length == 0 || subopts.vals.length > 1) {
                                    attrElem = $('<li class="w2editablePivot_container_header_grandTotal axis_s dummyAttr"></li>').append($('<span class="pvtAttr">' + w2parent.options.grandTotalName + '</span>').data("attrName", w2parent.options.grandTotalName));
                                } else {
                                    attrElem = $('<li class="w2editablePivot_container_header_grandTotal axis_s"></li>').append($('<span class="pvtAttr">' + w2parent.options.grandTotalName + '</span>').data("attrName", w2parent.options.grandTotalName));
                                }
                                if (valuesAxis === 'row') {
                                    if (subopts.cols.length == 0) {
                                        selectorStr = ".pvtAxisContainer.pvtCols";
                                        subopts.cols.push(opts.grandTotalName);
                                    } else {
                                        if (subopts.rows.length == 0 || subopts.vals.length > 1) {
                                            selectorStr = ".pvtAxisContainer.pvtRows";
                                            subopts.rows.push(opts.grandTotalName);
                                        }
                                    }
                                } else {
                                    if (subopts.rows.length == 0) {
                                        selectorStr = ".pvtAxisContainer.pvtRows";
                                        subopts.rows.push(opts.grandTotalName);
                                    } else {
                                        selectorStr = ".pvtAxisContainer.pvtCols";
                                        subopts.cols.push(opts.grandTotalName);
                                    }
                                }
                                valList = _this.find(selectorStr);
                                valList.append(attrElem);
                                subopts.valuesAxis = valuesAxis;
                                _this.data('sPosition', valuesAxis);
                            } else {
                                if (valuesAxis === 'row') {
                                    if ((_.indexOf(subopts.rows, opts.grandTotalName) != -1 && subopts.rows.length > 1 && subopts.vals.length > 1) ||
                                        (_.indexOf(subopts.cols, opts.grandTotalName) != -1 && subopts.cols.length > 1 && subopts.vals.length > 1)) {
                                        selectorStr = ' li.axis_s';
                                        if (_.indexOf(subopts.rows, opts.grandTotalName) != -1) {
                                            if (sPosition === 'row') {
                                                i = subopts.rows.indexOf(opts.grandTotalName);
                                                subopts.rows.splice(i, 1);
                                                selectorStr = '.pvtRows' + selectorStr;
                                            } else if (sPosition === 'col') {
                                                i = subopts.cols.indexOf(opts.grandTotalName);
                                                subopts.cols.splice(i, 1);
                                                selectorStr = '.pvtCols' + selectorStr;
                                            }
                                            _this.find(selectorStr).remove();
                                        } else {
                                            if (sPosition === 'row') {
                                                i = subopts.cols.indexOf(opts.grandTotalName);
                                                subopts.cols.splice(i, 1);
                                                selectorStr = '.pvtCols' + selectorStr;
                                            } else if (sPosition === 'col') {
                                                i = subopts.rows.indexOf(opts.grandTotalName);
                                                subopts.rows.splice(i, 1);
                                                selectorStr = '.pvtRows' + selectorStr;
                                            }
                                            _this.find(selectorStr).remove();
                                        }
                                    }
                                    if (subopts.rows.length == 0 || subopts.cols.length == 0 || subopts.vals.length > 1) {
                                        attrElem = $('<li class="w2editablePivot_container_header_grandTotal axis_s dummyAttr"></li>').append($('<span class="pvtAttr">' + w2parent.options.grandTotalName + '</span>').data("attrName", w2parent.options.grandTotalName));
                                    } else {
                                        attrElem = $('<li class="w2editablePivot_container_header_grandTotal axis_s"></li>').append($('<span class="pvtAttr">' + w2parent.options.grandTotalName + '</span>').data("attrName", w2parent.options.grandTotalName));
                                    }
                                    if (subopts.cols.length == 0) {
                                        selectorStr = ".pvtAxisContainer.pvtCols";
                                        subopts.cols.push(opts.grandTotalName);
                                    } else if (subopts.rows.length == 0 || (subopts.vals.length > 1) && document.getElementsByClassName("dummyAttr").length == 0) {
                                        selectorStr = ".pvtAxisContainer.pvtRows";
                                        subopts.rows.push(opts.grandTotalName);
                                    }
                                    if (selectorStr) {
                                        valList = _this.find(selectorStr);
                                        valList.append(attrElem);
                                    }
                                }
                                subopts.valuesAxis = sPosition;
                            }
                            if (e) {
                                droped = $(e.originalEvent.target);
                                if (droped.text() === opts.grandTotalName) {
                                    dest = droped.closest('div');
                                    if (dest.hasClass('pvtCols')) {
                                        subopts.valuesAxis = 'col';
                                        _this.data('sPosition', subopts.valuesAxis);
                                    } else if (dest.hasClass('pvtRows')) {
                                        subopts.valuesAxis = 'row';
                                        _this.data('sPosition', subopts.valuesAxis);
                                    } else if (dest.hasClass('pvtUnused')) {
                                        subopts.valuesAxis = null;
                                        subopts.vals = [];
                                        _this.data('sPosition', subopts.valuesAxis);
                                        droped.closest('li').remove();
                                        unUsedArea = _this.find('.pvtUnused');
                                        $fields = _this.find('.pvtVals li');
                                        for (i = $fields.length - 1; i > -1; i--) {
                                            $field = $($fields[i]);
                                            textNode = $field.find('.pvtAttr')[0].firstChild;
                                            textNode.deleteData(0, textNode.data.indexOf(': ') + 2);
                                            $field.find('.pvtTriangle').hide();
                                            $field.addClass('valueItem');
                                            $field.prependTo(unUsedArea);
                                        }
                                    } else {
                                        subopts.valuesAxis = null;
                                        subopts.vals = [];
                                        _this.data('sPosition', subopts.valuesAxis);
                                        droped.closest('li').remove();
                                    }
                                    _this.find(".pvtAxisContainer").sortable('refreshPositions');
                                }
                            }
                        } else {
                            if (sPosition) {
                                var removeDummyAttr = true;
                                if ((subopts.cols.length == 0) || (subopts.rows.length == 0)) {
                                    if (subopts.vals.length > 0) {
                                        if (subopts.rows.length == 0) {
                                            selectorStr = ".pvtAxisContainer.pvtRows";
                                            subopts.rows.push(opts.grandTotalName);
                                            valList = _this.find(selectorStr);
                                            valList.append(attrElem);
                                        } else if (subopts.cols.length == 0) {
                                            selectorStr = ".pvtAxisContainer.pvtCols";
                                            subopts.cols.push(opts.grandTotalName);
                                            valList = _this.find(selectorStr);
                                            valList.append(attrElem);
                                        }
                                        removeDummyAttr = false;
                                    }
                                } else {
                                    if ((_.indexOf(subopts.rows, opts.grandTotalName) != -1 && subopts.rows.length > 1) ||
                                        (_.indexOf(subopts.cols, opts.grandTotalName) != -1 && subopts.cols.length > 1)) {
                                        removeDummyAttr = true;
                                    } else {
                                        removeDummyAttr = false;
                                    }
                                }
                                if (removeDummyAttr == true) {
                                    selectorStr = ' li.axis_s';
                                    if (sPosition === 'row') {
                                        var i = subopts.rows.indexOf(opts.grandTotalName);
                                        var j = subopts.cols.indexOf(opts.grandTotalName);
                                        if (i >= 0) {
                                            subopts.rows.splice(i, 1);
                                            selectorStr = '.pvtRows' + selectorStr;
                                        } else if (j >= 0) {
                                            subopts.cols.splice(j, 1);
                                            selectorStr = '.pvtCols' + selectorStr;
                                        }
                                    } else if (sPosition === 'col') {
                                        var i = subopts.cols.indexOf(opts.grandTotalName);
                                        subopts.cols.splice(i, 1);
                                        selectorStr = '.pvtCols' + selectorStr;
                                    }
                                    subopts.valuesAxis = null;
                                    _this.data('sPosition', subopts.valuesAxis);
                                    _this.find(selectorStr).remove();
                                }
                                _this.find(".pvtAxisContainer").sortable('refreshPositions');
                            } else {
                                subopts.valuesAxis = valuesAxis;
                            }
                        }
                        subopts.aggregatorName = opts.aggregatorName;
                        subopts.aggregator = opts.aggregators[subopts.aggregatorName];
                        subopts.aggregatorMap = _.clone(opts.aggregators);
                        subopts.aggregatorList = _.clone(opts.aggregatorList);
                        // subopts.renderer = opts.renderers[renderer.val()];
                        subopts.originalViewOptions = opts.originalViewOptions;
                        subopts.rendererOptions.attributeLabels = opts.attributeLabels;
                        exclusions = {};
                        inclusions = {};
                        _.each(opts.inclusionList, function(inclusionMap, key1) {
                            var allMap = axisValues[key1];
                            _.each(allMap, function(value, key2) {
                                if (inclusionMap[key2]) {
                                    if (inclusionMap[key1] != null) {
                                        return inclusions[key1].push(key2);
                                    } else {
                                        return inclusions[key1] = [key2];
                                    }
                                } else {
                                    if (exclusions[key1] != null) {
                                        return exclusions[key1].push(key2);
                                    } else {
                                        return exclusions[key1] = [key2];
                                    }
                                }
                            });
                        });
                        _.each(opts.exclusionList, function(exclusionMap, key1) {
                            var allMap = axisValues[key1];
                            _.each(allMap, function(value, key2) {
                                if (exclusionMap[key2]) {
                                    if (exclusions[key1] != null) {
                                        return exclusions[key1].push(key2);
                                    } else {
                                        return exclusions[key1] = [key2];
                                    }
                                } else {
                                    if (inclusions[key1] != null) {
                                        return inclusions[key1].push(key2);
                                    } else {
                                        return inclusions[key1] = [key2];
                                    }
                                }
                            });
                        });
                        subopts.filter = function(record) {
                            var excludedItems, incluedItems, ref7, matched = false;
                            if (!opts.filter(record)) {
                                return false;
                            }
                            for (m in inclusions) {
                                incluedItems = inclusions[m];
                                ref7 = "" + record[m];
                                if (indexOf.call(incluedItems, ref7) >= 0) {
                                    //return true;
                                    matched = true;
                                } else {
                                    return false;
                                }
                            }
                            if (matched == true) {
                                return true;
                            }
                            for (k in exclusions) {
                                excludedItems = exclusions[k];
                                ref7 = "" + record[k];
                                if (indexOf.call(excludedItems, ref7) >= 0) {
                                    return false;
                                }
                            }
                            return true;
                        };
                        if (_.size(opts.exclusionList) == 0 && _.size(exclusions) != 0)
                            opts.exclusionList = _.reduce(exclusions, function(memo, list, key) {
                                memo[key] = _.reduce(list, function(memo2, value) {
                                    memo2[value] = value;
                                    return memo2;
                                }, {});
                                return memo;
                            }, {});
                        pivotTable.pivot(input, subopts, null, null, w2parent);
                        //_this.data( 'public.api', pivotTable.pivot(input, subopts) );
                        pivotUIOptions = $.extend(opts, {
                            cols: subopts.cols,
                            rows: subopts.rows,
                            vals: subopts.vals,
                            exclusions: exclusions,
                            inclusions: inclusions,
                            inclusionsInfo: inclusions,
                            aggregatorName: subopts.aggregatorName,
                            //rendererName: renderer.val(),
                            valuesAxis: subopts.valuesAxis,
                            aggregatorList: subopts.aggregatorList,
                            attributeLabels: subopts.rendererOptions.attributeLabels
                            //,
                            //input: input
                        });
                        _this.data("pivotUIOptions", pivotUIOptions);
                        if (opts.autoSortUnusedAttrs) {
                            unusedAttrsContainer = _this.find("div.pvtUnused.pvtAxisContainer");
                            $(unusedAttrsContainer).children("li").sort(function(a, b) {
                                return naturalSort($(a).text(), $(b).text());
                            }).appendTo(unusedAttrsContainer);
                        }
                        pivotTable.css("opacity", 1);
                        if (opts.onRefresh != null) {
                            return opts.onRefresh(pivotUIOptions);
                        }
                    };
                })(this);
                refresh = (function() {
                    return function(e, ui, moveAttrToContentBody) {
                        if (pivotTable) {
                            pivotTable.css("opacity", 0.5);
                            return setTimeout(function() {
                                refreshDelayed(e, ui, w2parent, moveAttrToContentBody);
                            }, 10);
                        }
                    };
                })(this);
                refresh();
                this.find(".pvtAxisContainer").sortable({
                    update: function(e, ui) {
                        var $containedTD,
                            target = e.originalEvent.target,
                            textNode,
                            $pvtAttr,
                            attrName,
                            idx = -1;
                        if (ui.sender == null) {
                            $containedTD = $(target).closest('div');
                            $tableContentBody = $(target).closest('th');
                            if (ui.item.hasClass('dummyAttr')) {
                                return false;
                            }
                            if (ui.item.hasClass('rejectDragToHidden') && $containedTD.hasClass('pvtUnused')) {
                                alert(opts.localeStrings.Pivot_canNotMoveToHiddenList);
                                return false;
                            }
                            if ($tableContentBody.length > 0) {
                                if ($tableContentBody.hasClass('pvtAxisLabel')) {
                                    if (ui.item.data('movable') == true) {
                                        return refresh(e, ui, true);
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } else {
                                if (ui.item.hasClass('axis_s') && $containedTD.hasClass('pvtVals')) {
                                    alert(opts.localeStrings.dragError);
                                    return false;
                                } else {
                                    if ($(e.target).hasClass('pvtVals')) {
                                        textNode = target.firstChild;
                                        while (textNode.nodeType != 3 && textNode.firstChild) {
                                            textNode = textNode.firstChild;
                                        }
                                        idx = textNode.data.indexOf(': ');
                                        if (idx > -1) {
                                            textNode.deleteData(0, idx + 2);
                                        }
                                    }
                                    if ($containedTD.hasClass('pvtUnused')) {
                                        ui.item.find('.pvtTriangle').hide();
                                        ui.item.removeClass("pvtFilteredAttribute");
                                        ui.item.addClass('valueItem');
                                    } else {
                                        $pvtAttr = ui.item.find('.pvtAttr');
                                        attrName = $pvtAttr.data('attrName');
                                        if ($containedTD.hasClass('pvtVals')) {
                                            // setAggregatorName( $pvtAttr, attrName );
                                            ui.item.addClass('valueItem');
                                            ui.item.find('.pvtTriangle').hide();
                                            ui.item.unbind("dblclick");
                                        } else {
                                            ui.item.find('.pvtTriangle').show();
                                            if (!(opts.exclusionList[attrName] && !_.isEmpty(opts.exclusionList[attrName]))) {
                                                ui.item.removeClass('valueItem');
                                                ui.item.addClass('pvtFilteredAttribute');
                                                ui.item.unbind("dblclick");
                                                ui.item.bind("dblclick", showFilterList);
                                            }
                                        }
                                    }
                                    return refresh(e, ui);
                                }
                            }
                        }
                    },
                    start: function(e, ui) {
                        var $containedTD,
                            target = e.originalEvent.target;
                        $containedTD = $(target).closest('div');
                        if ($containedTD.hasClass('pvtUnused')) {
                            ui.item.data('movable', true);
                        } else {
                            ui.item.data('movable', false);
                        }
                    },
                    change: function(e, ui) {
                        if (ui.placeholder.index() < 1 && !$(ui.placeholder).hasClass('pvtAxisContainer')) {
                            $(ui.placeholder).css('display', 'none');
                        } else {
                            $(ui.placeholder).css('display', '');
                        }
                    },
                    connectWith: ".pvtAxisContainer, .pvtAxisLabel",
                    items: 'li',
                    placeholder: 'pvtPlaceholder'
                });
            } catch (_error) {
                e = _error;
                if (typeof console !== "undefined" && console !== null) {
                    console.error(e.stack);
                }
                this.html(opts.localeStrings.uiRenderError);
            }
            return this;
        };
        /*
        Heatmap post-processing
         */
        $.fn.heatmap = function(scope) {
            var colorGen, heatmapper, i, j, l, n, numCols, numRows, ref, ref1;
            if (scope == null) {
                scope = "heatmap";
            }
            numRows = this.data("numrows");
            numCols = this.data("numcols");
            colorGen = function(color, min, max) {
                var hexGen;
                hexGen = (function() {
                    switch (color) {
                        case "red":
                            return function(hex) {
                                return "ff" + hex + hex;
                            };
                        case "green":
                            return function(hex) {
                                return hex + "ff" + hex;
                            };
                        case "blue":
                            return function(hex) {
                                return "" + hex + hex + "ff";
                            };
                    }
                })();
                return function(x) {
                    var hex, intensity;
                    intensity = 255 - Math.round(255 * (x - min) / (max - min));
                    hex = intensity.toString(16).split(".")[0];
                    if (hex.length === 1) {
                        hex = 0 + hex;
                    }
                    return hexGen(hex);
                };
            };
            heatmapper = (function(_this) {
                return function(scope, color) {
                    var colorFor, forEachCell, values;
                    forEachCell = function(f) {
                        return _this.find(scope).each(function() {
                            var x;
                            x = $(this).data("value");
                            if ((x != null) && isFinite(x)) {
                                return f(x, $(this));
                            }
                        });
                    };
                    values = [];
                    forEachCell(function(x) {
                        return values.push(x);
                    });
                    colorFor = colorGen(color, Math.min.apply(Math, values), Math.max.apply(Math, values));
                    return forEachCell(function(x, elem) {
                        return elem.css("background-color", "#" + colorFor(x));
                    });
                };
            })(this);
            switch (scope) {
                case "heatmap":
                    heatmapper(".pvtVal", "red");
                    break;
                case "rowheatmap":
                    for (i = l = 0, ref = numRows; 0 <= ref ? l < ref : l > ref; i = 0 <= ref ? ++l : --l) {
                        heatmapper(".pvtVal.row" + i, "red");
                    }
                    break;
                case "colheatmap":
                    for (j = n = 0, ref1 = numCols; 0 <= ref1 ? n < ref1 : n > ref1; j = 0 <= ref1 ? ++n : --n) {
                        heatmapper(".pvtVal.col" + j, "red");
                    }
            }
            heatmapper(".pvtTotal.rowTotal", "red");
            heatmapper(".pvtTotal.colTotal", "red");
            return this;
        };
        /*
        Barchart post-processing
         */
        return $.fn.barchart = function() {
            var barcharter, i, l, numCols, numRows, ref;
            numRows = this.data("numrows");
            numCols = this.data("numcols");
            barcharter = (function(_this) {
                return function(scope) {
                    var forEachCell, max, scaler, values;
                    forEachCell = function(f) {
                        return _this.find(scope).each(function() {
                            var x;
                            x = $(this).data("value");
                            if ((x != null) && isFinite(x)) {
                                return f(x, $(this));
                            }
                        });
                    };
                    values = [];
                    forEachCell(function(x) {
                        return values.push(x);
                    });
                    max = Math.max.apply(Math, values);
                    scaler = function(x) {
                        return 100 * x / (1.4 * max);
                    };
                    return forEachCell(function(x, elem) {
                        var text, wrapper;
                        text = elem.text();
                        wrapper = $("<div>").css({
                            "position": "relative",
                            "height": "55px"
                        });
                        wrapper.append($("<div>").css({
                            "position": "absolute",
                            "bottom": 0,
                            "left": 0,
                            "right": 0,
                            "height": scaler(x) + "%",
                            "background-color": "gray"
                        }));
                        wrapper.append($("<div>").text(text).css({
                            "position": "relative",
                            "padding-left": "5px",
                            "padding-right": "5px"
                        }));
                        return elem.css({
                            "padding": 0,
                            "padding-top": "5px",
                            "text-align": "center"
                        }).html(wrapper);
                    });
                };
            })(this);
            for (i = l = 0, ref = numRows; 0 <= ref ? l < ref : l > ref; i = 0 <= ref ? ++l : --l) {
                barcharter(".pvtVal.row" + i);
            }
            barcharter(".pvtTotal.colTotal");
            return this;
        };
    });
}).call(this);