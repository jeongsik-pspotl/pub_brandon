(function() {
    var t, e, r, n = [].indexOf || function(t) {
            for (var e = 0, r = this.length; r > e; e++)
                if (e in this && this[e] === t) return e;
            return -1
        },
        a = [].slice,
        s = function(t, e) {
            return function() {
                return t.apply(e, arguments)
            }
        },
        i = {}.hasOwnProperty,
        o = "\u03a3\uac12";
    (t = function(t) {
        return "object" == typeof exports && "object" == typeof module ? t(require("jquery")) : "function" == typeof define && define.amd ? define(["jquery"], t) : t(jQuery, _, e, r)
    })(function(t, e, r, l) {
        var u, c, p, h, d, f, g, v, m, A, b, x, w, C, T, y, L, N;
        return c = function(t, e, r) {
            var n, a, s, i;
            for (t += "", a = t.split("."), s = a[0], i = a.length > 1 ? r + a[1] : "", n = /(\d+)(\d{3})/; n.test(s);) s = s.replace(n, "$1" + e + "$2");
            return s + i
        }, b = function(e) {
            var r;
            return r = {
                    digitsAfterDecimal: 2,
                    scaler: 1,
                    thousandsSep: ",",
                    decimalSep: ".",
                    prefix: "",
                    suffix: "",
                    showZero: !1
                }, e = t.extend(r, e),
                function(t) {
                    var r;
                    return isNaN(t) || !isFinite(t) ? "" : 0 !== t || e.showZero ? (r = c((e.scaler * t).toFixed(e.digitsAfterDecimal), e.thousandsSep, e.decimalSep), "" + e.prefix + r + e.suffix) : ""
                }
        }, T = b(), y = b({
            digitsAfterDecimal: 0
        }), L = b({
            digitsAfterDecimal: 1,
            scaler: 100,
            suffix: "%"
        }), p = {
            count: function(t) {
                return null == t && (t = y),
                    function() {
                        return function() {
                            return {
                                count: 0,
                                push: function() {
                                    return this.count++
                                },
                                value: function() {
                                    return this.count
                                },
                                format: t
                            }
                        }
                    }
            },
            countUnique: function(t) {
                return null == t && (t = y),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    uniq: [],
                                    push: function(t) {
                                        var e = t[r];
                                        return n.call(this.uniq, e) < 0 ? this.uniq.push(t[r]) : void 0
                                    },
                                    value: function() {
                                        return this.uniq.length
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            listUnique: function(t) {
                return function(e) {
                    var r;
                    return r = e[0],
                        function() {
                            return {
                                uniq: [],
                                push: function(t) {
                                    var e = t[r];
                                    return n.call(this.uniq, e) < 0 ? this.uniq.push(t[r]) : void 0
                                },
                                value: function() {
                                    return this.uniq.join(t)
                                },
                                format: function(t) {
                                    return t
                                },
                                numInputs: null != r ? 0 : 1
                            }
                        }
                }
            },
            sum: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    sum: 0,
                                    push: function(t) {
                                        return isNaN(parseFloat(t[r])) ? void 0 : this.sum += parseFloat(t[r])
                                    },
                                    value: function() {
                                        return this.sum
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            min: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    val: null,
                                    push: function(t) {
                                        var e, n;
                                        return n = parseFloat(t[r]), isNaN(n) ? void 0 : this.val = Math.min(n, null != (e = this.val) ? e : n)
                                    },
                                    value: function() {
                                        return this.val
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            max: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    val: null,
                                    push: function(t) {
                                        var e, n;
                                        return n = parseFloat(t[r]), isNaN(n) ? void 0 : this.val = Math.max(n, null != (e = this.val) ? e : n)
                                    },
                                    value: function() {
                                        return this.val
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            average: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    sum: 0,
                                    len: 0,
                                    push: function(t) {
                                        return isNaN(parseFloat(t[r])) ? void 0 : (this.sum += parseFloat(t[r]), this.len++)
                                    },
                                    value: function() {
                                        return this.sum / this.len
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            sumOverSum: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r = e[0],
                            n = e[1];
                        return function() {
                            return {
                                sumNum: 0,
                                sumDenom: 0,
                                push: function(t) {
                                    return isNaN(parseFloat(t[r])) || (this.sumNum += parseFloat(t[r])), isNaN(parseFloat(t[n])) ? void 0 : this.sumDenom += parseFloat(t[n])
                                },
                                value: function() {
                                    return this.sumNum / this.sumDenom
                                },
                                format: t,
                                numInputs: null != r && null != n ? 0 : 2
                            }
                        }
                    }
            },
            sumOverSumBound80: function(t, e) {
                return null == t && (t = !0), null == e && (e = T),
                    function(r) {
                        var n = r[0],
                            a = r[1];
                        return function() {
                            return {
                                sumNum: 0,
                                sumDenom: 0,
                                push: function(t) {
                                    return isNaN(parseFloat(t[n])) || (this.sumNum += parseFloat(t[n])), isNaN(parseFloat(t[a])) ? void 0 : this.sumDenom += parseFloat(t[a])
                                },
                                value: function() {
                                    var e;
                                    return e = t ? 1 : -1, (.821187207574908 / this.sumDenom + this.sumNum / this.sumDenom + 1.2815515655446004 * e * Math.sqrt(.410593603787454 / (this.sumDenom * this.sumDenom) + this.sumNum * (1 - this.sumNum / this.sumDenom) / (this.sumDenom * this.sumDenom))) / (1 + 1.642374415149816 / this.sumDenom)
                                },
                                format: e,
                                numInputs: null != n && null != a ? 0 : 2
                            }
                        }
                    }
            },
            fractionOf: function(t, e, r) {
                return null == e && (e = "total"), null == r && (r = L),
                    function() {
                        var n;
                        return n = 1 <= arguments.length ? a.call(arguments, 0) : [],
                            function(a, s, i) {
                                return {
                                    selector: {
                                        total: [
                                            [],
                                            []
                                        ],
                                        row: [s, []],
                                        col: [
                                            [], i
                                        ]
                                    } [e],
                                    inner: t.apply(null, n)(a, s, i),
                                    push: function(t) {
                                        return this.inner.push(t)
                                    },
                                    format: r,
                                    value: function() {
                                        return this.inner.value() / a.getAggregator.apply(a, this.selector).inner.value()
                                    },
                                    numInputs: t.apply(null, n)().numInputs
                                }
                            }
                    }
            }
        }, h = function(t) {
            return {
                Count: t.count(y),
                "Count Unique Values": t.countUnique(y),
                "List Unique Values": t.listUnique(", "),
                Sum: t.sum(T),
                "Integer Sum": t.sum(y),
                Average: t.average(T),
                Minimum: t.min(T),
                Maximum: t.max(T),
                "Sum over Sum": t.sumOverSum(T),
                "80% Upper Bound": t.sumOverSumBound80(!0, T),
                "80% Lower Bound": t.sumOverSumBound80(!1, T),
                "Sum as Fraction of Total": t.fractionOf(t.sum(), "total", L),
                "Sum as Fraction of Rows": t.fractionOf(t.sum(), "row", L),
                "Sum as Fraction of Columns": t.fractionOf(t.sum(), "col", L),
                "Count as Fraction of Total": t.fractionOf(t.count(), "total", L),
                "Count as Fraction of Rows": t.fractionOf(t.count(), "row", L),
                "Count as Fraction of Columns": t.fractionOf(t.count(), "col", L)
            }
        }(p), w = {
            Table: function(t, e) {
                return x(t, e)
            },
            "Table Barchart": function(e, r) {
                return t(x(e, r)).barchart()
            },
            Heatmap: function(e, r) {
                return t(x(e, r)).heatmap()
            },
            "Row Heatmap": function(e, r) {
                return t(x(e, r)).heatmap("rowheatmap")
            },
            "Col Heatmap": function(e, r) {
                return t(x(e, r)).heatmap("colheatmap")
            }
        }, v = {
            en: {
                aggregators: h,
                renderers: w,
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
        }, m = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"], d = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"], N = function(t) {
            return ("0" + t).substr(-2, 2)
        }, f = {
            bin: function(t, e) {
                return function(r) {
                    return r[t] - r[t] % e
                }
            },
            dateFormat: function(t, e, r, n, a) {
                var s;
                return null == r && (r = !1), null == n && (n = m), null == a && (a = d), s = r ? "UTC" : "",
                    function(r) {
                        var i;
                        return i = new Date(Date.parse(r[t])), isNaN(i) ? "" : e.replace(/%(.)/g, function(t, e) {
                            switch (e) {
                                case "y":
                                    return i["get" + s + "FullYear"]();
                                case "m":
                                    return N(i["get" + s + "Month"]() + 1);
                                case "n":
                                    return n[i["get" + s + "Month"]()];
                                case "d":
                                    return N(i["get" + s + "Date"]());
                                case "w":
                                    return a[i["get" + s + "Day"]()];
                                case "x":
                                    return i["get" + s + "Day"]();
                                case "H":
                                    return N(i["get" + s + "Hours"]());
                                case "M":
                                    return N(i["get" + s + "Minutes"]());
                                case "S":
                                    return N(i["get" + s + "Seconds"]());
                                default:
                                    return "%" + e
                            }
                        })
                    }
            }
        }, A = function() {
            return function(t, e) {
                var r, n, a, s, i, o, l;
                if (o = /(\d+)|(\D+)/g, i = /\d/, l = /^0/, "number" == typeof t || "number" == typeof e) return isNaN(t) ? 1 : isNaN(e) ? -1 : t - e;
                if (r = String(t).toLowerCase(), a = String(e).toLowerCase(), r === a) return 0;
                if (!i.test(r) || !i.test(a)) return r > a ? 1 : -1;
                for (r = r.match(o), a = a.match(o); r.length && a.length;)
                    if (n = r.shift(), s = a.shift(), n !== s) return i.test(n) && i.test(s) ? n.replace(l, ".0") - s.replace(l, ".0") : n > s ? 1 : -1;
                return r.length - a.length
            }
        }(this), C = function(t) {
            var e, r, n;
            r = {};
            for (e in t) n = t[e], r[n] = e;
            return function(t, e) {
                return null != r[t] && null != r[e] ? r[t] - r[e] : null != r[t] ? -1 : null != r[e] ? 1 : A(t, e)
            }
        }, g = function(e, r) {
            var n;
            return n = e(r), t.isFunction(n) ? n : A
        }, t.pivotUtilities = {
            aggregatorTemplates: p,
            aggregators: h,
            renderers: w,
            derivers: f,
            locales: v,
            naturalSort: A,
            numberFormat: b,
            sortAs: C
        }, u = function() {
            function r(t, n) {
                if (this.getAggregator = s(this.getAggregator, this), this.getRowKeys = s(this.getRowKeys, this), this.getColKeys = s(this.getColKeys, this), this.sortKeys = s(this.sortKeys, this), this.arrSort = s(this.arrSort, this), this.aggregator = n.aggregator, this.aggregators = n.aggregators, this.aggregatorName = n.aggregatorName, this.colAttrs = n.cols, this.rowAttrs = n.rows, this.valAttrs = n.vals, this.sorters = n.sorters, this.tree = {}, this.rowKeys = [], this.colKeys = [], this.rowTotals = {}, this.colTotals = {}, this.allTotal = n.allTotals, this.sorted = !1, this.valuesAxis = n.valuesAxis || "row", "row" !== this.valuesAxis && 1 === this.valAttrs.length && 0 === this.rowAttrs.length && 0 === this.colAttrs.length && (this.valuesAxis = "row"), this.valAttrs) e.each(this.allTotal, function(t, e, r) {
                    r[e] = t(this, [], [])
                }, this), r.forEachRecord(t, n.derivedAttributes, function(t) {
                    return function(e) {
                        var r = [];
                        if (n.filter(e)) {
                            for (var a = 0; a < t.valAttrs.length; a++) r.push(t.processRecord(e, t.valAttrs[a]));
                            return r
                        }
                    }
                }(this));
                else
                    for (var a = 0, i = t.length; i > a; a++) this.processHeader(t[a])
            }
            return r.forEachRecord = function(e, r, n) {
                var a, s, o, l, u, c, p, h, d, f, g, v;
                if (a = t.isEmptyObject(r) ? n : function(t) {
                        var e, a, s;
                        for (e in r) s = r[e], t[e] = null != (a = s(t)) ? a : t[e];
                        return n(t)
                    }, t.isFunction(e)) return e(a);
                if (t.isArray(e)) {
                    if (t.isArray(e[0])) {
                        f = [];
                        for (o in e)
                            if (i.call(e, o) && (s = e[o], o > 0)) {
                                h = {}, d = e[0];
                                for (l in d) i.call(d, l) && (u = d[l], h[u] = s[l]);
                                f.push(a(h))
                            }
                        return f
                    }
                    for (g = [], c = 0, p = e.length; p > c; c++) h = e[c], Array.prototype.push.apply(g, a(h));
                    return g
                }
                if (e instanceof jQuery) return v = [], t("thead > tr > th", e).each(function() {
                    return v.push(t(this).text())
                }), t("tbody > tr", e).each(function() {
                    return h = {}, t("td", this).each(function(e) {
                        return h[v[e]] = t(this).html()
                    }), a(h)
                });
                throw new Error("unknown input format")
            }, r.convertToArray = function(t) {
                var e;
                return e = [], r.forEachRecord(t, {}, function(t) {
                    return e.push(t)
                }), e
            }, r.prototype.arrSort = function(t) {
                var e, r;
                return r = function() {
                        var r, n, a;
                        for (a = [], r = 0, n = t.length; n > r; r++) e = t[r], a.push(g(this.sorters, e));
                        return a
                    }.call(this),
                    function(t, e) {
                        var n, a, s;
                        for (a in r)
                            if (s = r[a], n = s(t[a], e[a]), 0 !== n) return n;
                        return 0
                    }
            }, r.prototype.sortKeys = function() {
                return this.sorted ? void 0 : (this.sorted = !0, this.rowKeys.sort(this.arrSort(this.rowAttrs)), this.colKeys.sort(this.arrSort(this.colAttrs)))
            }, r.prototype.getColKeys = function() {
                return this.sortKeys(), this.colKeys
            }, r.prototype.getRowKeys = function() {
                return this.sortKeys(), this.rowKeys
            }, r.prototype.processRecord = function(t, e) {
                var r, n, a, s, i, l, u, c, p, h, d, f;
                for (r = [], f = [], c = this.colAttrs, s = 0, i = c.length; i > s; s++) c[s] === o ? r.push(e) : (p = t[c[s]], null != p && r.push(p));
                for (h = this.rowAttrs, u = 0, l = h.length; l > u; u++) h[u] === o ? f.push(e) : (d = t[h[u]], null != d && f.push(d));
                return a = f.join(String.fromCharCode(0)), n = r.join(String.fromCharCode(0)), this.allTotal[e].push(t), 0 !== f.length && (this.rowTotals[a] || (this.rowTotals[a] = {}, this.rowKeys.push(f)), this.rowTotals[a][e] || (this.rowTotals[a][e] = this.aggregators[e](this, f, [])), this.rowTotals[a][e].push(t)), 0 !== r.length && (this.colTotals[n] || (this.colTotals[n] = {}, this.colKeys.push(r)), this.colTotals[n][e] || (this.colTotals[n][e] = this.aggregators[e](this, [], r)), this.colTotals[n][e].push(t)), 0 !== r.length && 0 !== f.length ? (this.tree[a] || (this.tree[a] = {}), this.tree[a][n] || (this.tree[a][n] = this.aggregators[e](this, f, r)), this.tree[a][n].push(t)) : void 0
            }, r.prototype.processHeader = function(t) {
                var e, r, n, a, s, i = [],
                    o = [];
                for (e = this.colAttrs, n = 0, r = e.length; r > n; n++) i.push(t[e[n]] || null);
                for (e = this.rowAttrs, n = 0, r = e.length; r > n; n++) o.push(t[e[n]] || null);
                a = o.join(String.fromCharCode(0)), s = i.join(String.fromCharCode(0)), 0 !== o.length && (this.rowTotals[a] || this.rowKeys.push(o)), 0 !== i.length && (this.colTotals[s] || this.colKeys.push(i))
            }, r.prototype.getAggregator = function(t, e, r) {
                var n, a, s;
                return s = t.join(String.fromCharCode(0)), a = e.join(String.fromCharCode(0)), n = 0 === t.length && 0 === e.length ? this.allTotal[r] : 0 === t.length ? this.colTotals[a][r] : 0 === e.length ? this.rowTotals[s][r] : this.tree[s][a], null != n ? n : {
                    value: function() {
                        return null
                    },
                    format: function() {
                        return ""
                    }
                }
            }, r
        }(), x = function(r, n) {
            var a, s, l, u, c, p, h, d, f, g, v, m, A, b, x, w, C, T, y, L, N, S, M;
            p = {
                localeStrings: {
                    totals: "Totals"
                },
                attributeLabels: {}
            }, n = t.extend(p, n), l = r.colAttrs, m = r.rowAttrs, b = r.getRowKeys(), c = r.getColKeys(), 0 === l.length && c.length > 0 && (l = e.flatten(c)), v = document.createElement("table"), v.id = "pvtTable", v.className = "pvtTable", x = function(t, e, r) {
                var n, a, s, i, o, l, u, c;
                if (0 !== e) {
                    for (i = !0, c = n = 0, o = r; o >= 0 ? o >= n : n >= o; c = o >= 0 ? ++n : --n) t[e - 1][c] !== t[e][c] && (i = !1);
                    if (i) return -1
                }
                for (a = 0; e + a < t.length;) {
                    for (u = !1, c = s = 0, l = r; l >= 0 ? l >= s : s >= l; c = l >= 0 ? ++s : --s) t[e][c] !== t[e + a][c] && (u = !0);
                    if (u) break;
                    a++
                }
                return a
            };
            for (d in l)
                if (i.call(l, d)) {
                    s = l[d], y = document.createElement("tr"), 0 === parseInt(d) && 0 !== m.length && (C = document.createElement("th"), C.setAttribute("colspan", m.length), C.setAttribute("rowspan", l.length), y.appendChild(C)), C = document.createElement("th"), C.className = "pvtAxisLabel", C.innerHTML = n.attributeLabels[s] || s, y.appendChild(C);
                    for (h in c) i.call(c, h) && (u = c[h], S = x(c, parseInt(h), parseInt(d)), -1 !== S && (C = document.createElement("th"), C.className = "pvtColLabel", C.innerHTML = n.attributeLabels[u[d]] || u[d], C.setAttribute("colspan", S), parseInt(d) === l.length - 1 && 0 !== m.length && C.setAttribute("rowspan", 2), y.appendChild(C)));
                    if (0 === parseInt(d))
                        if ("col" === r.valuesAxis) {
                            if (r.colAttrs.length > 1)
                                for (f = 0; f < r.valAttrs.length; f++) C = document.createElement("th"), C.className = "pvtTotalLabel", C.innerHTML = n.attributeLabels[r.valAttrs[f]] || r.valAttrs[f], C.setAttribute("rowspan", l.length + (0 === m.length ? 0 : 1)), y.appendChild(C)
                        } else r.colAttrs.length > 0 && (C = document.createElement("th"), C.className = "pvtTotalLabel", C.innerHTML = n.localeStrings.totals, C.setAttribute("rowspan", l.length + (0 === m.length ? 0 : 1)), y.appendChild(C));
                    v.appendChild(y)
                }
            if (0 !== m.length) {
                y = document.createElement("tr");
                for (h in m) i.call(m, h) && (g = m[h], C = document.createElement("th"), C.className = "pvtAxisLabel", C.innerHTML = n.attributeLabels[g] || g, y.appendChild(C));
                C = document.createElement("th"), 0 === l.length && (C.className = "pvtTotalLabel", C.innerHTML = g !== o && "col" === r.valuesAxis && r.valAttrs[0] ? n.attributeLabels[r.valAttrs[0]] : n.localeStrings.totals), y.appendChild(C), v.appendChild(y)
            }
            if (r.valAttrs)
                for (h in b)
                    if (i.call(b, h)) {
                        A = b[h], y = document.createElement("tr");
                        for (d in A) i.call(A, d) && (L = A[d], S = x(b, parseInt(h), parseInt(d)), -1 !== S && (C = document.createElement("th"), C.className = "pvtRowLabel", C.innerHTML = n.attributeLabels[L] || L, C.setAttribute("rowspan", S), parseInt(d) === m.length - 1 && 0 !== l.length && C.setAttribute("colspan", 2), y.appendChild(C)));
                        for (d in c) i.call(c, d) && (u = c[d], a = r.getAggregator(A, u), N = a.value(), w = document.createElement("td"), w.className = "pvtVal row" + h + " col" + d, w.innerHTML = a.format(N), w.setAttribute("data-value", N), y.appendChild(w));
                        if ("col" === r.valuesAxis) {
                            if (r.colAttrs.length > 1 || 0 === r.colAttrs.length && 1 === r.rowAttrs.length && 1 === r.valAttrs.length)
                                for (f = 0; f < r.valAttrs.length; f++) T = r.getAggregator(A, [], r.valAttrs[f]), N = T.value(), w = document.createElement("td"), w.className = "pvtTotal rowTotal", w.innerHTML = T.format(N), w.setAttribute("data-value", N), w.setAttribute("data-for", "row" + h), y.appendChild(w)
                        } else M = e.indexOf(m, o), T = r.getAggregator(A, [], -1 === M ? r.valAttrs[0] : A[M]), N = T.value(), w = document.createElement("td"), w.className = "pvtTotal rowTotal", w.innerHTML = T.format(N), w.setAttribute("data-value", N), w.setAttribute("data-for", "row" + h), y.appendChild(w);
                        v.appendChild(y)
                    }
            if ("col" === r.valuesAxis) {
                y = document.createElement("tr"), C = document.createElement("th"), C.className = "pvtTotalLabel", C.innerHTML = n.localeStrings.totals, C.setAttribute("colspan", m.length + (0 === l.length ? 0 : 1)), y.appendChild(C), M = e.indexOf(l, o) || 0;
                for (d in c) i.call(c, d) && (u = c[d], T = r.getAggregator([], u, u[M]), N = T.value(), w = document.createElement("td"), w.className = "pvtTotal colTotal", w.innerHTML = T.format(N), w.setAttribute("data-value", N), w.setAttribute("data-for", "col" + d), y.appendChild(w));
                if (r.colAttrs.length > 1 || 1 === r.valAttrs.length && (0 === r.colAttrs.length || 0 === r.rowAttrs.length))
                    for (h = 0; h < r.valAttrs.length; h++) T = r.getAggregator([], [], r.valAttrs[h]), N = T.value(), w = document.createElement("td"), w.className = "pvtGrandTotal", w.innerHTML = T.format(N), w.setAttribute("data-value", N), y.appendChild(w);
                v.appendChild(y)
            } else if (r.rowAttrs.length > 1 || 1 === r.valAttrs.length && (0 === r.colAttrs.length || 0 === r.rowAttrs.length))
                for (h = 0; h < r.valAttrs.length; h++) {
                    y = document.createElement("tr"), C = document.createElement("th"), C.className = "pvtTotalLabel", C.innerHTML = n.attributeLabels[r.valAttrs[h]] || r.valAttrs[h], C.setAttribute("colspan", m.length + (0 === l.length ? 0 : 1)), y.appendChild(C);
                    for (d in c) i.call(c, d) && (u = c[d], T = r.getAggregator([], u, r.valAttrs[h]), N = T.value(), w = document.createElement("td"), w.className = "pvtTotal colTotal", w.innerHTML = T.format(N), w.setAttribute("data-value", N), w.setAttribute("data-for", "col" + d), y.appendChild(w));
                    T = r.getAggregator([], [], r.valAttrs[h]), N = T.value(), w = document.createElement("td"), w.className = "pvtGrandTotal", w.innerHTML = T.format(N), w.setAttribute("data-value", N), y.appendChild(w), v.appendChild(y)
                }
            return v.setAttribute("data-numrows", b.length), v.setAttribute("data-numcols", c.length), v
        }, t.fn.pivot = function(r, n) {
            var a, s, i, l, c;
            if (n = n || {}, n.locale || (n.locale = "ko"), a = {
                    cols: [],
                    rows: [],
                    vals: [],
                    valuesAxis: "row",
                    filter: function() {
                        return !0
                    },
                    aggregatorName: "Count",
                    aggregatorList: {},
                    sorters: function() {},
                    derivedAttributes: {},
                    renderer: x,
                    rendererOptions: {
                        localeStrings: v[n.locale].localeStrings
                    },
                    localeStrings: v[n.locale].localeStrings
                }, n = t.extend(a, n), n.aggregator || (n.aggregator = v[n.locale].aggregators[n.aggregatorName]), n.aggregatorMap || (n.aggregatorMap = e.clone(v[n.locale].aggregators)), n.attributeLabels && (n.rendererOptions.attributeLabels = n.attributeLabels), l = null, n.cols && n.cols.length > 0 || n.rows && n.rows.length > 0 || n.vals && n.vals.length > 0) {
                n.aggregators = {}, n.allTotals = {};
                for (var p = 0; p < n.vals.length; p++) n.aggregators[n.vals[p]] = n.aggregatorList[n.vals[p]] ? n.aggregatorMap[n.aggregatorList[n.vals[p]]]([n.vals[p]]) : n.aggregator([n.vals[p]]), n.allTotals[n.vals[p]] = n.aggregatorList[n.vals[p]] ? n.aggregatorMap[n.aggregatorList[n.vals[p]]]([n.vals[p]]) : n.aggregator([n.vals[p]]);
                n.vals.length > 1 && ("row" === n.valuesAxis ? -1 === e.indexOf(n.rows, o) && n.rows.push(o) : -1 === e.indexOf(n.cols, o) && n.cols.push(o));
                try {
                    i = new u(r, n);
                    try {
                        l = n.renderer(i, n.rendererOptions)
                    } catch (h) {
                        s = h, "undefined" != typeof console && null !== console && console.error(s.stack), l = t("<span>").html(n.localeStrings.renderError)
                    }
                } catch (h) {
                    s = h, "undefined" != typeof console && null !== console && console.error(s.stack), l = t("<span>").html(n.localeStrings.computeError)
                }
                c && c.remove(), this.html(""), this.append(l)
            } else this.html("");
            return {
                originalView: c
            }
        }, t.fn.pivotUI = function(r, a, s, l) {
            var c, p, h, d, f, m, b, x, w, C, T, y, L, N, S, M, O, E, F, R, D, I, V, H, k, K, U, q, P, j, B, _, z, J, G, Q, W, Z, $, Y, X, tt, et, rt;
            null == s && (s = !1), null == l && (l = "en"), m = {
                derivedAttributes: {},
                attributeLabels: {},
                aggregatorName: "Count",
                aggregators: v[l].aggregators,
                aggregatorList: {},
                renderers: v[l].renderers,
                hiddenAttributes: [],
                menuLimit: 200,
                cols: [],
                rows: [],
                vals: [],
                exclusions: {},
                unusedAttrsVertical: "auto",
                autoSortUnusedAttrs: !1,
                rendererOptions: {
                    localeStrings: v[l].localeStrings
                },
                onRefresh: null,
                filter: function() {
                    return !0
                },
                sorters: function() {},
                localeStrings: v[l].localeStrings
            }, x = this.data("pivotUIOptions"), R = null == x || s ? t.extend(m, a) : x, this.data("sPosition") && this.data("sPosition", null);
            try {
                e.isArray(r) || e.isObject(r[0]) || (r = u.convertToArray(r)), _ = function() {
                    var t, e;
                    t = r[0], e = [];
                    for (y in t) i.call(t, y) && e.push(y);
                    return e
                }(), V = R.derivedAttributes;
                for (d in V) i.call(V, d) && n.call(_, d) < 0 && _.push(d);
                for (h = {}, L = 0, N = _.length; N > L; L++) W = _[L], h[W] = {};
                u.forEachRecord(r, R.derivedAttributes, function(t) {
                    var e, r, n;
                    r = [];
                    for (y in t) i.call(t, y) && (n = t[y], R.filter(t) && (null == n && (n = "null"), null == (e = h[y])[n] && (e[n] = 0), r.push(h[y][n]++)));
                    return r
                }), G = t("<table>").attr("cellpadding", 5), j = t('<td class="alignCenter">'), P = t("<select>").addClass("pvtRenderer").appendTo(j).bind("change", function() {
                    return U()
                }), H = R.renderers;
                for (W in H) i.call(H, W) && t("<option>").val(W).html(W).appendTo(P);
                if (f = t("<td>").addClass("pvtAxisContainer pvtUnused"), B = function() {
                        var t, e, r;
                        for (r = [], e = 0, t = _.length; t > e; e++) d = _[e], n.call(R.hiddenAttributes, d) < 0 && r.push(d);
                        return r
                    }(), Q = !1, "auto" === R.unusedAttrsVertical) {
                    for (p = 0, E = 0, S = B.length; S > E; E++) c = B[E], p += c.length;
                    Q = p > 120
                }
                R.unusedAttrsVertical === !0 || Q ? (j.attr("colspan", 3), f.addClass("pvtVertList")) : (j.attr("rowspan", 3), f.addClass("pvtHorizList")), R.exclusionList = e.reduce(R.exclusions, function(t, r, n) {
                    return t[n] = e.reduce(r, function(t, e) {
                        return t[e] = e, t
                    }, {}), t
                }, {}), $ = function(e) {
                    var r, n, a, s, i, o, l, u, c;
                    if (r = function() {
                            var t;
                            t = [];
                            for (l in h[e.c]) t.push(l);
                            return t
                        }(), Z.html(""), Z.append(t("<h4>").text(e.d + " (" + r.length + ")")), r.length > R.menuLimit) Z.append(t("<p>").html(R.localeStrings.tooMany));
                    else
                        for (n = t("<p>").appendTo(Z), n.append(t("<button>", {
                                type: "button"
                            }).html(R.localeStrings.selectAll).bind("click", function() {
                                return Z.find("input:visible").prop("checked", !0)
                            })), n.append(t("<button>", {
                                type: "button"
                            }).html(R.localeStrings.selectNone).bind("click", function() {
                                return Z.find("input:visible").prop("checked", !1)
                            })), n.append(t("<input>", {
                                type: "text",
                                placeholder: R.localeStrings.filterResults,
                                "class": "pvtSearch"
                            }).bind("keyup", function() {
                                var e;
                                return e = t(this).val().toLowerCase(), Z.find(".pvtCheckContainer p").each(function() {
                                    var r;
                                    return r = t(this).text().toLowerCase().indexOf(e), -1 !== r ? t(this).show() : t(this).hide()
                                })
                            })), a = t("<div>").addClass("pvtCheckContainer").appendTo(Z), s = r.sort(g(R.sorters, e.c)), i = 0, o = s.length; o > i; i++) l = s[i], u = h[e.c][l], c = t("<label>"), t("<input>").attr("type", "checkbox").addClass("pvtFilter").attr("checked", R.exclusionList[e.c] ? !R.exclusionList[e.c][l] : !0).data("filter", [e.c, l]).appendTo(c), c.append(t("<span>").html(l)), c.append(t("<span>").text(" (" + u + ")")), a.append(t("<p>").append(c))
                }, Y = function(r) {
                    var n;
                    Z.html(""), Z.append(t("<h4>").text(r.d)), n = t('<select class="pvtAggregator"></select>'), e.each(R.aggregators, function(e, r) {
                        n.append(t("<option>").val(r).html(r))
                    }), Z.append(n), R.aggregatorList[r.c] ? n.val(R.aggregatorList[r.c]) : null != R.aggregatorName && n.val(R.aggregatorName)
                }, tt = function(t, e) {
                    t[0].firstChild.insertData(0, R.aggregatorList[e] ? R.aggregatorList[e] + ": " : R.aggregatorName + ": ")
                }, et = function() {
                    var e, r, n, a, s = 0,
                        i = Z.find(".pvtAggregator").val(),
                        o = Z.data("openItem");
                    return "pvtVals" === o.area ? (r = Z.prev("table").find("td." + o.area + " li." + o.item + " .pvtAttr"), n = r[0].firstChild, n.replaceData(0, n.data.indexOf(": ") + 2, i + ": "), R.aggregatorList[r.data("attrName")] = i, Z.toggle(0, U)) : (a = Z.find("[type='checkbox']"), a.each(function(r, n) {
                        var a, i = t(n);
                        a || (a = i.data("filter")), 0 === r && (R.exclusionList[a[0]] = {}), t(n).is(":checked") ? s += 1 : (e = !0, R.exclusionList[a[0]][a[1]] = a[1])
                    }), r = Z.prev("table").find("td." + o.area + " li." + o.item), e ? r.addClass("pvtFilteredAttribute") : r.removeClass("pvtFilteredAttribute"), s > R.menuLimit ? Z.toggle() : Z.toggle(0, U))
                }, rt = function(e) {
                    var r, n, a, s, i, o = {},
                        l = t(e.target);
                    return l.closest("td").hasClass("pvtUnused") ? !1 : (l.hasClass("pvtTriangle") && (l = l.parent("span")), r = l.data("attrName"), o.item = l.closest("li").attr("class"), o.item = o.item.split(/\s+/)[0], n = l.closest("td").attr("class"), n.indexOf("pvtVals") > -1 ? o.area = "pvtVals" : n.indexOf("pvtCols") > -1 ? o.area = "pvtCols" : n.indexOf("pvtRows") > -1 && (o.area = "pvtRows"), Z.data("openItem", o), "pvtVals" === o.area ? Y({
                        c: r,
                        d: R.attributeLabels[r] || r
                    }) : $({
                        c: r,
                        d: R.attributeLabels[r] || r
                    }), t("<p>").appendTo(Z).append(t("<button>", {
                        type: "button"
                    }).text("적용").bind("click", et)).append(t("<button>", {
                        type: "button"
                    }).text("닫기").bind("click", function() {
                        Z.toggle(0)
                    })), a = l.find(".pvtTriangle").position(), s = l.closest(".w2pivotTable"), i = s.width() - (a.left + Z.width() + 20), 0 > i && (a.left += i), i = s.height() - (a.top + Z.height() + 20), 0 > i && (a.top += i), Z.css({
                        left: a.left,
                        top: a.top
                    }).show(), void 0)
                }, w = function(e) {
                    var r, n;
                    return n = t("<span>").addClass("pvtTriangle").html(" &#x25BE;").bind("click", rt).hide(), r = t("<li>").addClass("axis_" + C).append(t("<span>").addClass("pvtAttr").text(e.d).data("attrName", e.c).append(n)), f.append(r), r.bind("dblclick", rt)
                };
                for (C in B) i.call(B, C) && (d = B[C], d = {
                    c: d,
                    d: R.attributeLabels[d] || d
                }, w(d));
                for (z = t("<tr>").appendTo(G), t("<td>").addClass("pvtAxisContainer pvtHorizList pvtCols").appendTo(z), J = t("<tr>").appendTo(G), J.append(t("<td>").addClass("pvtAxisContainer pvtRows").attr("valign", "top")), D = t("<td>").attr("valign", "top").addClass("pvtRendererArea").appendTo(J), R.unusedAttrsVertical === !0 || Q ? (G.find("tr:nth-child(1)").prepend(j), G.find("tr:nth-child(2)").prepend(t("<td style='min-width: 170px;'>").addClass("pvtAxisContainer pvtVertList pvtVals")), G.find("tr:nth-child(2)").prepend(f)) : (G.prepend(t("<tr>").append(t("<td style='height: 17px;'>").addClass("pvtAxisContainer pvtHorizList pvtVals"))), G.prepend(t("<tr>").append(j).append(f))), this.html(G), Z = t("<div>").addClass("pvtFilterBox").hide(), this.append(Z), X = function(r, n, a) {
                        var s = null;
                        return s = this.find(".axis_" + t.inArray(r, n)), a ? tt(s.find(".pvtAttr"), r) : R.exclusionList[r] && !e.isEmpty(R.exclusionList[r]) && s.addClass("pvtFilteredAttribute"), s.find(".pvtTriangle").show(), s
                    }, k = R.cols, F = 0, M = k.length; M > F; F++) W = k[F], this.find(".pvtCols").append(X.call(this, W, B, !1));
                for (K = R.rows, I = 0, O = K.length; O > I; I++) W = K[I], this.find(".pvtRows").append(X.call(this, W, B, !1));
                for (K = R.vals, I = 0, O = K.length; O > I; I++) W = K[I], this.find(".pvtVals").append(X.call(this, W, B, !0));
                null != R.rendererName && this.find(".pvtRenderer").val(R.rendererName), T = !0, q = function(a) {
                    return function(s, i) {
                        var l, u, c, p, d, f, g, v, m, b, x, w, C, T, L, N, S = R.valuesAxis || "row";
                        if (p = {
                                derivedAttributes: R.derivedAttributes,
                                localeStrings: R.localeStrings,
                                rendererOptions: R.rendererOptions || {},
                                sorters: R.sorters,
                                cols: [],
                                rows: [],
                                vals: []
                            }, a.find(".pvtRows li span.pvtAttr").each(function() {
                                return p.rows.push(t(this).data("attrName"))
                            }), a.find(".pvtCols li span.pvtAttr").each(function() {
                                return p.cols.push(t(this).data("attrName"))
                            }), a.find(".pvtVals li span.pvtAttr").each(function() {
                                return p.vals.push(t(this).data("attrName"))
                            }), f = a.data("sPosition"), p.vals.length > 1) {
                            if (f) p.valuesAxis = f;
                            else {
                                g = t('<li class="axis_s"></li>').append(t('<span class="pvtAttr">' + o + "</span>").data("attrName", o));
                                var M = -1;
                                "row" === S ? (L = ".pvtAxisContainer.pvtRows", M = e.findIndex(R.rows, function(t) {
                                    return t === o
                                }), M >= 0 ? p.rows.splice(M, 0, o) : p.rows.push(o)) : (L = ".pvtAxisContainer.pvtCols", M = e.findIndex(R.cols, function(t) {
                                    return t === o
                                }), M >= 0 ? p.cols.splice(M, 0, o) : p.cols.push(o)), v = a.find(L), M >= 0 ? g.insertBefore(v.children().eq(M)) : v.append(g), p.valuesAxis = S, a.data("sPosition", S)
                            }
                            if (s && (m = t(s.originalEvent.target), m.text() === o)) {
                                if (b = m.closest("td"), b.hasClass("pvtCols")) p.valuesAxis = "col", a.data("sPosition", p.valuesAxis);
                                else if (b.hasClass("pvtRows")) p.valuesAxis = "row", a.data("sPosition", p.valuesAxis);
                                else if (b.hasClass("pvtUnused"))
                                    for (p.valuesAxis = null, p.vals = [], a.data("sPosition", p.valuesAxis), m.closest("li").remove(), x = a.find(".pvtUnused"), w = a.find(".pvtVals li"), T = w.length - 1; T > -1; T--) C = t(w[T]), N = C.find(".pvtAttr")[0].firstChild, N.deleteData(0, N.data.indexOf(": ") + 2), C.find(".pvtTriangle").hide(), C.prependTo(x);
                                else p.valuesAxis = null, p.vals = [], a.data("sPosition", p.valuesAxis), m.closest("li").remove();
                                a.find(".pvtAxisContainer").sortable("refreshPositions")
                            }
                        } else f ? (L = " li.axis_s", "row" === f ? (T = p.rows.indexOf(o), p.rows.splice(T, 1), L = ".pvtRows" + L) : "col" === f && (T = p.cols.indexOf(o), p.cols.splice(T, 1), L = ".pvtCols" + L), p.valuesAxis = null, a.data("sPosition", p.valuesAxis), a.find(L).remove(), a.find(".pvtAxisContainer").sortable("refreshPositions")) : p.valuesAxis = S;
                        return p.aggregatorName = R.aggregatorName, p.aggregator = R.aggregators[p.aggregatorName], p.aggregatorMap = e.clone(R.aggregators), p.aggregatorList = e.clone(R.aggregatorList), p.renderer = R.renderers[P.val()], p.originalViewOptions = R.originalViewOptions, p.rendererOptions.attributeLabels = R.attributeLabels, l = {}, u = {}, e.each(R.exclusionList, function(t, r) {
                            var n = h[r];
                            e.each(n, function(e, n) {
                                return t[n] ? null != l[r] ? l[r].push(n) : l[r] = [n] : null != u[r] ? u[r].push(n) : u[r] = [n]
                            })
                        }), p.filter = function(t) {
                            var e, r;
                            if (!R.filter(t)) return !1;
                            for (y in l)
                                if (e = l[y], r = "" + t[y], n.call(e, r) >= 0) return !1;
                            return !0
                        }, D.pivot(r, p), c = t.extend(R, {
                            cols: p.cols,
                            rows: p.rows,
                            vals: p.vals,
                            exclusions: l,
                            inclusionsInfo: u,
                            aggregatorName: p.aggregatorName,
                            rendererName: P.val(),
                            valuesAxis: p.valuesAxis,
                            aggregatorList: p.aggregatorList,
                            attributeLabels: p.rendererOptions.attributeLabels
                        }), a.data("pivotUIOptions", c), R.autoSortUnusedAttrs && (d = a.find("td.pvtUnused.pvtAxisContainer"), t(d).children("li").sort(function(e, r) {
                            return A(t(e).text(), t(r).text())
                        }).appendTo(d)), D.css("opacity", 1), null != R.onRefresh ? R.onRefresh(c) : void 0
                    }
                }(this), U = function() {
                    return function(t, e) {
                        return D.css("opacity", .5), setTimeout(function() {
                            q(t, e)
                        }, 10)
                    }
                }(this), U(), this.find(".pvtAxisContainer").sortable({
                    update: function(r, n) {
                        var a, s, i, o, l = r.originalEvent.target,
                            u = -1;
                        if (null == n.sender) {
                            if (a = t(l).closest("td"), n.item.hasClass("axis_s") && a.hasClass("pvtVals")) return alert(R.localeStrings.dragError), !1;
                            if (t(r.target).hasClass("pvtVals")) {
                                for (s = l.firstChild; 3 != s.nodeType && s.firstChild;) s = s.firstChild;
                                u = s.data.indexOf(": "), u > -1 && s.deleteData(0, u + 2)
                            }
                            return a.hasClass("pvtUnused") ? (n.item.find(".pvtTriangle").hide(), n.item.removeClass("pvtFilteredAttribute")) : (i = n.item.find(".pvtAttr"), o = i.data("attrName"), a.hasClass("pvtVals") ? tt(i, o) : R.exclusionList[o] && !e.isEmpty(R.exclusionList[o]) && n.item.addClass("pvtFilteredAttribute"), n.item.find(".pvtTriangle").show()), U(r, n)
                        }
                    },
                    connectWith: this.find(".pvtAxisContainer"),
                    items: "li",
                    placeholder: "pvtPlaceholder"
                })
            } catch (nt) {
                b = nt, "undefined" != typeof console && null !== console && console.error(b.stack), this.html(R.localeStrings.uiRenderError)
            }
            return this
        }, t.fn.heatmap = function(e) {
            var r, n, a, s, i, o, l, u, c, p;
            switch (null == e && (e = "heatmap"), u = this.data("numrows"), l = this.data("numcols"), r = function(t, e, r) {
                    var n;
                    return n = function() {
                            switch (t) {
                                case "red":
                                    return function(t) {
                                        return "ff" + t + t
                                    };
                                case "green":
                                    return function(t) {
                                        return t + "ff" + t
                                    };
                                case "blue":
                                    return function(t) {
                                        return "" + t + t + "ff"
                                    }
                            }
                        }(),
                        function(t) {
                            var a, s;
                            return s = 255 - Math.round(255 * (t - e) / (r - e)), a = s.toString(16).split(".")[0], 1 === a.length && (a = 0 + a), n(a)
                        }
                }, n = function(e) {
                    return function(n, a) {
                        var s, i, o;
                        return i = function(r) {
                            return e.find(n).each(function() {
                                var e;
                                return e = t(this).data("value"), null != e && isFinite(e) ? r(e, t(this)) : void 0
                            })
                        }, o = [], i(function(t) {
                            return o.push(t)
                        }), s = r(a, Math.min.apply(Math, o), Math.max.apply(Math, o)), i(function(t, e) {
                            return e.css("background-color", "#" + s(t))
                        })
                    }
                }(this), e) {
                case "heatmap":
                    n(".pvtVal", "red");
                    break;
                case "rowheatmap":
                    for (a = i = 0, c = u; c >= 0 ? c > i : i > c; a = c >= 0 ? ++i : --i) n(".pvtVal.row" + a, "red");
                    break;
                case "colheatmap":
                    for (s = o = 0, p = l; p >= 0 ? p > o : o > p; s = p >= 0 ? ++o : --o) n(".pvtVal.col" + s, "red")
            }
            return n(".pvtTotal.rowTotal", "red"), n(".pvtTotal.colTotal", "red"), this
        }, t.fn.barchart = function() {
            var e, r, n, a, s, i;
            for (s = this.data("numrows"), a = this.data("numcols"), e = function(e) {
                    return function(r) {
                        var n, a, s, i;
                        return n = function(n) {
                            return e.find(r).each(function() {
                                var e;
                                return e = t(this).data("value"), null != e && isFinite(e) ? n(e, t(this)) : void 0
                            })
                        }, i = [], n(function(t) {
                            return i.push(t)
                        }), a = Math.max.apply(Math, i), s = function(t) {
                            return 100 * t / (1.4 * a)
                        }, n(function(e, r) {
                            var n, a;
                            return n = r.text(), a = t("<div>").css({
                                position: "relative",
                                height: "55px"
                            }), a.append(t("<div>").css({
                                position: "absolute",
                                bottom: 0,
                                left: 0,
                                right: 0,
                                height: s(e) + "%",
                                "background-color": "gray"
                            })), a.append(t("<div>").text(n).css({
                                position: "relative",
                                "padding-left": "5px",
                                "padding-right": "5px"
                            })), r.css({
                                padding: 0,
                                "padding-top": "5px",
                                "text-align": "center"
                            }).html(a)
                        })
                    }
                }(this), r = n = 0, i = s; i >= 0 ? i > n : n > i; r = i >= 0 ? ++n : --n) e(".pvtVal.row" + r);
            return e(".pvtTotal.colTotal"), this
        }
    })
}).call(this);